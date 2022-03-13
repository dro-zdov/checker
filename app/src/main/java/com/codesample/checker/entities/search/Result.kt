package com.codesample.checker.entities.search

data class Result(
    val count: Int,
    val expanded_count: Int,
    val items: List<Item>,
    val mainCount: Int,
    val nextPageId: String,
    val totalCount: Int
)