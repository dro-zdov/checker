package com.codesample.checker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.codesample.checker.entities.details.AdDetails
import com.codesample.checker.repo.AvitoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class AdDetailsViewModel @Inject constructor(
    private val repo: AvitoRepository
) : ViewModel() {
    private var details: LiveData<AdDetails>? = null

    fun getAdDetails(id: Long) = details ?: liveData(Dispatchers.IO) {
        emit(repo.getAdDetails(id))
    }.also { details = it }

}