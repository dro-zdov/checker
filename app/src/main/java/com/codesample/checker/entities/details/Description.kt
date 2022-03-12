package com.codesample.checker.entities.details

data class Description(
    val displaying: Displaying,
    val id: Int,
    val placeholder: String,
    val required: Boolean,
    val title: String,
    val type: String
)