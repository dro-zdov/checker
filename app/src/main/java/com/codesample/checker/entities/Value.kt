package com.codesample.checker.entities

data class Value(
    val address: String,
    val category: Category,
    val contactlessView: Boolean,
    val coords: Coords,
    val delivery: Delivery,
    val hasVideo: Boolean,
    val id: Long,
    val images: Images,
    val isFavorite: Boolean,
    val isVerified: Boolean,
    //val list: List<>,
    val location: String,
    val price: String,
    val sellerInfo: SellerInfoX,
    val time: Int,
    val title: String,
    val uri: String,
    val uri_mweb: String,
    val userType: String
)