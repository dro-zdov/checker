package com.codesample.checker.entities.suggestion

data class SuggestionRequest(
    val key: String,
    val presentationType: String,
    val query: String
)