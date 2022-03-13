package com.codesample.checker.repo

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.codesample.checker.entities.search.Item
import com.codesample.checker.services.AvitoService

class SearchAdsPagingSource(
    private val service: AvitoService,
    private val query: String?,
    private val key: String
): PagingSource<Int, Item>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Item> {
        if (query == null) {
            return LoadResult.Page(
                data = listOf(),
                prevKey = null,
                nextKey = null
            )
        }
        else try {
            val pageNumber = params.key ?: 1
            val loadSize = params.loadSize
            val response = service.searchAds(
                query,
                pageNumber,
                loadSize,
                key
            )
            val items = response.result.items.filter { it.type == "item" }

            return LoadResult.Page(
                data = items,
                prevKey = if (pageNumber > 1) pageNumber - 1 else null,
                nextKey = if (items.isNotEmpty()) pageNumber + 1 else null
            )
        }
        catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Item>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}