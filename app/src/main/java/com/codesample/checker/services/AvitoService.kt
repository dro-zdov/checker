package com.codesample.checker.services

import com.codesample.checker.entities.AvitoResponce
import com.codesample.checker.entities.suggestion.SuggestionRequest
import com.codesample.checker.entities.suggestion.SuggestionResult
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AvitoService {
    @GET("api/11/items")
    suspend fun searchAds(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("key") key: String,
        @Query("locationId") locationId: String,
    ): AvitoResponce

    @POST("api/2/suggest/mav")
    suspend fun searchSuggestions(
        @Query("key") key: String,
        @Body request: SuggestionRequest,
    ): SuggestionResult

    companion object {
        private const val BASE_URL = "https://m.avito.ru/"

        fun create(): AvitoService {
            val logger = HttpLoggingInterceptor().apply { level = Level.BODY }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AvitoService::class.java)
        }
    }
}