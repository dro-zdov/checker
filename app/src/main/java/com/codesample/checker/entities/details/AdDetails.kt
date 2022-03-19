package com.codesample.checker.entities.details

data class AdDetails(
    val id: Long,
    val address: String?,
    val description: String?,
    val images: List<Image>?,
    val price: Price?,
    val title: String?,
    val time: Int?
)