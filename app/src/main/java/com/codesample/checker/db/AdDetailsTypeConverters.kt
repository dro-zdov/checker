package com.codesample.checker.db

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.codesample.checker.entities.details.Image
import com.codesample.checker.entities.details.Price
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@ProvidedTypeConverter
class AdDetailsTypeConverters(val gson: Gson) {
    @TypeConverter
    fun stringToImages(json: String?): List<Image>? {
        return if (json == null) {
            null
        }
        else {
            gson.fromJson<List<Image>>(json,  object: TypeToken<List<Image>>() {}.type)
        }
    }

    @TypeConverter
    fun imagesToString(images: List<Image>?): String? {
        return if (images == null) {
            null
        }
        else {
            gson.toJson(images)
        }
    }

    @TypeConverter
    fun stringToPrice(json: String?): Price? {
        return if (json == null) {
            null
        }
        else {
            gson.fromJson<Price>(json,  object: TypeToken<Price>() {}.type)
        }
    }

    @TypeConverter
    fun priceToString(price: Price?): String? {
        return if (price == null) {
            null
        }
        else {
            gson.toJson(price)
        }
    }
}