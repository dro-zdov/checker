package com.codesample.checker.entities.suggestion

data class SuggestionRequest(
    val key: String,
    val locationId: Int,
    val presentationType: String,
    val query: String
)