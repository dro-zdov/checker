package com.codesample.checker.entities.search

import com.google.gson.annotations.SerializedName

data class Main(
    @field:SerializedName("100x75") val img100x75: String,
    @field:SerializedName("1280x960") val img1280x960: String,
    @field:SerializedName("140x105") val img140x105: String,
    @field:SerializedName("208x156") val img208x156: String,
    @field:SerializedName("240x180") val img240x180: String,
    @field:SerializedName("288x216") val img288x216: String,
    @field:SerializedName("432x324") val img432x324: String,
    @field:SerializedName("640x480") val img640x480: String,
    @field:SerializedName("75x55") val img75x55: String,
    @field:SerializedName("80x60") val img80x60: String
)