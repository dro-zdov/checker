package com.codesample.checker.viewmodels

import android.content.Context
import androidx.lifecycle.*
import androidx.work.*
import com.codesample.checker.entities.db.AdDetailsContainer
import com.codesample.checker.entities.details.AdDetails
import com.codesample.checker.repo.AdDetailsRepository
import com.codesample.checker.repo.AvitoRepository
import com.codesample.checker.workers.DeleteAdDetailsWorker
import com.codesample.checker.workers.SaveAdDetailsWorker
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AdDetailsViewModel @Inject constructor(
    @ApplicationContext private val  context: Context,
    private val remoteRepo: AvitoRepository,
    private val localRepo: AdDetailsRepository,
    private val gson: Gson
) : ViewModel() {
    private var details: LiveData<List<AdDetailsContainer>>? = null

    fun getAdDetails(id: Long) = details ?: getAdDetailsInternal(id).also { details = it }

    private fun getAdDetailsInternal(id: Long): LiveData<List<AdDetailsContainer>> {
        return Transformations.switchMap(localRepo.getHistory(id)) {
            if (it.isEmpty()) { // No db entries, ad is not tracked
                getAdDetailsNetwork(id)
            }
            else {
                MutableLiveData(it)
            }
        }
    }

    private fun getAdDetailsNetwork(id: Long) = liveData(Dispatchers.IO) {
        val adDetails = remoteRepo.getAdDetails(id)
        emit(listOf(adDetails))
    }

    fun saveAdDetails(adDetails: AdDetails) {
        viewModelScope.launch {
            val json = withContext(Dispatchers.Default) {
                gson.toJson(adDetails)
            }

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresStorageNotLow(true)
                .build()

            val request = OneTimeWorkRequestBuilder<SaveAdDetailsWorker>()
                .setInputData(workDataOf(SaveAdDetailsWorker.KEY_AD_DETAILS to json))
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "save_${adDetails.id}",
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }

    fun deleteAdDetails(adDetails: AdDetails) {
        val request = OneTimeWorkRequestBuilder<DeleteAdDetailsWorker>()
            .setInputData(workDataOf(DeleteAdDetailsWorker.KEY_AD_DETAILS_ID to adDetails.id))
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "delete_${adDetails.id}",
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
}