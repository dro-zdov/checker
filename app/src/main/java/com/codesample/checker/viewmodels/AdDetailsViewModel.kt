package com.codesample.checker.viewmodels

import androidx.lifecycle.*
import com.codesample.checker.entities.db.AdDetailsContainer
import com.codesample.checker.entities.details.AdDetails
import com.codesample.checker.repo.AdDetailsRepository
import com.codesample.checker.repo.AvitoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdDetailsViewModel @Inject constructor(
    private val remoteRepo: AvitoRepository,
    private val localRepo: AdDetailsRepository
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
            localRepo.insert(adDetails)
        }
    }

    fun deleteAdDetails(adDetails: AdDetails) {
        viewModelScope.launch {
            localRepo.delete(adDetails)
        }
    }
}