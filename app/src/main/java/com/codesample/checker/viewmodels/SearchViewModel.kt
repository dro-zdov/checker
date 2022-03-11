package com.codesample.checker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codesample.checker.repo.AvitoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repo: AvitoRepository
) : ViewModel() {
    private val mSuggestions = MutableLiveData<List<String>>()
    private var mSuggestionsJob : Job? = null

    val suggestions: LiveData<List<String>> get() = mSuggestions

    fun searchSuggestions(query: String?) {
        mSuggestionsJob?.cancel() // Cancel previous job, if any
        if (query == null || query.isEmpty() ) {
            mSuggestions.value = listOf()
        }
        else {
            mSuggestionsJob = viewModelScope.launch(Dispatchers.IO) {
                delay(200) // Debounce
                val result = try {
                    repo.searchSuggestions(query)
                }
                catch (e: Exception) {
                    listOf()
                }
                withContext(Dispatchers.Main) {
                    mSuggestions.value = result
                }
            }
        }
    }
}