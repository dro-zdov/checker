package com.codesample.checker.entities.details

data class Price(
    val id: Int,
    val inputType: String,
    val motivation: Motivation,
    val placeholder: String,
    val required: Boolean,
    val title: String,
    val type: String
)