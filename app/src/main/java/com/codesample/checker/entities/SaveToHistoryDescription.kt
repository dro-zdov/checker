package com.codesample.checker.entities

data class SaveToHistoryDescription(
    val deeplink: String,
    val description: String,
    val rootCategoryId: Any,
    val title: String,
    val verticalCategoryId: Any
)