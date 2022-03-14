package com.codesample.checker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.codesample.checker.repo.AdDetailsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TrackedListViewModel @Inject constructor(
    private val repo: AdDetailsRepository
) : ViewModel() {
    val allLatest = repo.getAllLatest().cachedIn(viewModelScope)
}