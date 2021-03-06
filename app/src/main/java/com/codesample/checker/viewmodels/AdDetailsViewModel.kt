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
    private var adDetailsId = MutableLiveData<Long?>(null)

    fun setId(id: Long) {
        adDetailsId.value = id
    }

    val history: LiveData<DataOrException> = Transformations.switchMap(adDetailsId) { id ->
        if (id != null) {
            val dbHistory = try {
                localRepo.getHistory(id)
            }
            catch (e: Exception) {
                return@switchMap MutableLiveData(DataOrException(e))
            }
            Transformations.switchMap(dbHistory) { dbHistory ->
                if (dbHistory.isEmpty()) { // No db entries, ad is not tracked
                    liveData {
                        try {
                            val response = withContext(Dispatchers.IO) {
                                remoteRepo.getAdDetails(id)
                            }
                            emit(DataOrException(listOf(response)))
                        }
                        catch (e: Exception) {
                            emit(DataOrException(e))
                        }
                    }
                } else {
                    MutableLiveData(DataOrException(dbHistory))
                }
            }
        }
        else {
            MutableLiveData(DataOrException(listOf()))
        }
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

    data class DataOrException(
        val data: List<AdDetailsContainer>?,
        val exception: Exception?
    ) {
        constructor(data: List<AdDetailsContainer>): this(data, null)
        constructor(exception: Exception): this(null, exception)
    }
}