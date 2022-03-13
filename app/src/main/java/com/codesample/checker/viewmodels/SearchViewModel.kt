package com.codesample.checker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.codesample.checker.entities.search.Item
import com.codesample.checker.repo.AvitoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repo: AvitoRepository
) : ViewModel() {
    private val mSuggestions = MutableLiveData<List<String>>()
    private var mSuggestionsJob: Job? = null
    private var mLastPagingData: Flow<PagingData<Item>> = getEmptyPaging()
    private var mLastQuery: String? = null

    val suggestions: LiveData<List<String>> get() = mSuggestions
    val lastQuery: String? get() = mLastQuery

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

    fun searchAds(query: String?): Flow<PagingData<Item>> {
        if (mLastQuery != query) {
            mLastQuery = query
            mLastPagingData = repo.getSearchAdsStream(query).cachedIn(viewModelScope)
        }
        return mLastPagingData
    }

    private fun getEmptyPaging() = repo.getSearchAdsStream(null).cachedIn(viewModelScope)
}