package com.codesample.checker.viewmodels

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.codesample.checker.entities.search.Item
import com.codesample.checker.repo.AvitoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repo: AvitoRepository
) : ViewModel() {
    private val suggestionText = MutableLiveData<String?>()

    fun setSuggestionText(text: String?) {
        suggestionText.value = text
    }

    val suggestions: LiveData<List<String>> = Transformations.switchMap(suggestionText) { text ->
        liveData {
            if (text.isNullOrEmpty()) {
                emit(listOf())
            }
            else try {
                delay(200) // Debounce
                if (suggestionText.value == text) {
                    val suggestions = withContext(Dispatchers.IO) {
                        repo.searchSuggestions(text)
                    }
                    emit(suggestions)
                }
            }
            catch (e: Exception) {
                emit(listOf())
            }
        }
    }

    private val queryText = MutableStateFlow<String?>(null)

    fun setQueryText(text: String?) {
        queryText.value = text
    }

    val items = queryText.flatMapLatest { text ->
        repo.getSearchAdsStream(text)
    }.cachedIn(viewModelScope)

}