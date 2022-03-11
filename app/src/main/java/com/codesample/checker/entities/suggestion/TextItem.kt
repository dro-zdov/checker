package com.codesample.checker.entities.suggestion

data class TextItem(
    val actions: List<Action>,
    val description: String,
    val padding: Padding,
    val title: String,
    val uri: String
)