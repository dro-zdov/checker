package com.codesample.checker.entities

data class ValueX(
    val address: String,
    val category: CategoryX,
    val contactlessView: Boolean,
    val coords: CoordsX,
    val delivery: DeliveryX,
    val hasVideo: Boolean,
    val id: Long,
    val images: ImagesX,
    val isFavorite: Boolean,
    val isVerified: Boolean,
    val location: String,
    val price: String,
    val sellerInfo: SellerInfo,
    val services: List<String>,
    val time: Int,
    val title: String,
    val uri: String,
    val uri_mweb: String,
    val userType: String
)