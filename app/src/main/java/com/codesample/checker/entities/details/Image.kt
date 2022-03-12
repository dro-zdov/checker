package com.codesample.checker.entities.details

import com.google.gson.annotations.SerializedName

data class Image(
    @field:SerializedName("100x75") val img100x75: String,
    @field:SerializedName("1280x960")val img1280x960: String,
    @field:SerializedName("100x140x105")val img140x105: String,
    @field:SerializedName("240x180")val img240x180: String,
    @field:SerializedName("432x324")val img432x324: String,
    @field:SerializedName("640x480")val img640x480: String
)