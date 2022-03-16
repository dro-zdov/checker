package com.codesample.checker.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.codesample.checker.entities.db.AdDetailsContainer
import com.codesample.checker.entities.search.Item
import com.codesample.checker.entities.suggestion.SuggestionRequest
import com.codesample.checker.services.AvitoService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class AvitoRepository @Inject constructor(
    private val service: AvitoService,
    private val key: String
) {
    fun getSearchAdsStream(query: String?): Flow<PagingData<Item>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 30, initialLoadSize = 30, prefetchDistance = 10),
            pagingSourceFactory = { SearchAdsPagingSource(service, query, key) }
        ).flow
    }

    suspend fun getAdDetails(id: Long) = AdDetailsContainer(service.getAdDetails(id, key))

    suspend fun searchSuggestions(query: String): List<String> {
        val body = SuggestionRequest(
            key,
            "serp",
            query
        )
        val response = service.searchSuggestions(key, body)
        return response.result.map { it.text_item.title }
    }

    suspend fun downloadFile(url: String, file: File) = withContext(Dispatchers.IO) {
        try {
            val response = service.downloadFile(url)
            response.body()!!.byteStream().use { input ->
                FileOutputStream(file).use { output ->
                    val buffer = ByteArray(4096)
                    var read = 0
                    while (input.read(buffer).also { read = it } != -1) {
                        output.write(buffer, 0, read)
                    }
                }
            }
        }
        catch (e: Exception) {
            file.delete()
            throw e
        }
    }
}