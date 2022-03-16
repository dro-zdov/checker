package com.codesample.checker.services

import com.codesample.checker.entities.search.AvitoResponce
import com.codesample.checker.entities.details.AdDetails
import com.codesample.checker.entities.suggestion.SuggestionRequest
import com.codesample.checker.entities.suggestion.SuggestionResult
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface AvitoService {
    @GET("api/11/items")
    suspend fun searchAds(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("key") key: String,
    ): AvitoResponce

    @GET("api/11/items/{id}")
    suspend fun getAdDetails(
        @Path("id") id: Long,
        @Query("key") key: String,
    ): AdDetails

    @POST("api/2/suggest/mav")
    suspend fun searchSuggestions(
        @Query("key") key: String,
        @Body request: SuggestionRequest,
    ): SuggestionResult

    @GET
    suspend fun downloadFile(
        @Url url: String
    ): Response<ResponseBody>

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