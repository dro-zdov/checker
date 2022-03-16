package com.codesample.checker.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.codesample.checker.db.AdDetailsDao
import com.codesample.checker.entities.db.AdDetailsContainer
import com.codesample.checker.entities.details.AdDetails
import com.codesample.checker.entities.search.Item
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AdDetailsRepository @Inject constructor(private val adDetailsDao: AdDetailsDao) {

    suspend fun insert(vararg details: AdDetailsContainer) = adDetailsDao.insert(*details)

    suspend fun delete(details: AdDetails) = adDetailsDao.delete(details.id)

    fun getHistory(id: Long) = adDetailsDao.getHistory(id)

    fun getAllLatest(): Flow<PagingData<AdDetailsContainer>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 30),
            pagingSourceFactory = { adDetailsDao.getAllLatest() }
        ).flow
    }

    fun getAllLatestList(): List<AdDetailsContainer> = adDetailsDao.getAllLatestList()

}