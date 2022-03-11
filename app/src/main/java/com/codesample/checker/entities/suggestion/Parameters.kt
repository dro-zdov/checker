package com.codesample.checker.entities.suggestion

data class Parameters(
    val sgt_item_type: String,
    val sgt_source_query: String,
    val sgt_user_query: String,
    val sgtp: Int,
    val suggest_l1_source: String,
    val x_sgt: String
)