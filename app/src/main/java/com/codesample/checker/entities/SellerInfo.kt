package com.codesample.checker.entities

data class SellerInfo(
    val date: String,
    val isShop: Boolean,
    val link: String,
    val logo: Logo,
    val name: String,
    val rating: Rating,
    val sellerTypeName: String,
    val userKey: String
)