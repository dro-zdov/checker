package com.codesample.checker.repo

import com.codesample.checker.entities.suggestion.SuggestionRequest
import com.codesample.checker.services.AvitoService
import javax.inject.Inject

class AvitoRepository @Inject constructor(
    private val service: AvitoService,
    private val key: String
) {
    suspend fun searchSuggestions(query: String): List<String> {
        //TODO: Replace hardcoded locationId
        val body = SuggestionRequest(
            key,
            654070,
            "serp",
            query
        )
        val response = service.searchSuggestions(key, body)
        return response.result.map { it.text_item.title }
    }
}