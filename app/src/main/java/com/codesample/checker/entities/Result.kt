package com.codesample.checker.entities

data class Result(
    val applyButton: ApplyButton,
    val count: Int,
    val displayType: String,
    val expanded_count: Int,
    val items: List<Item>,
    val lastStamp: Int,
    val mainCount: Int,
    val nextPageId: String,
    val saveToHistoryDescription: SaveToHistoryDescription,
    val searchHint: String,
    val searchSubscriptionAction: String,
    val seo: Seo,
    val seoNavigation: SeoNavigation,
    val shouldShowSaveSearch: Boolean,
    val totalCount: Int,
    val witchersCount: Int,
    val xHash: String
)