package com.codesample.checker.db

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.codesample.checker.entities.db.AdDetailsContainer

@Dao
interface AdDetailsDao {
    @Insert
    suspend fun insert(vararg details: AdDetailsContainer)

    @Query(DELETE_BY_ID_QUERY)
    suspend fun delete(id: Long)

    @Query(ALL_HISTORY_QUERY)
    fun getHistory(id: Long): LiveData<List<AdDetailsContainer>>

    @Query(ALL_HISTORY_QUERY)
    suspend fun getHistoryList(id: Long): List<AdDetailsContainer>

    @Query(ALL_LATEST_QUERY)
    fun getAllLatest(): PagingSource<Int, AdDetailsContainer>

    @Query(ALL_LATEST_QUERY)
    suspend fun getAllLatestList(): List<AdDetailsContainer>

    companion object Queries {
        private const val ALL_LATEST_QUERY = """
            WITH id_and_max_time AS (
                SELECT id, MAX(time) AS time 
                FROM ad_details
                GROUP BY id
            )
            SELECT ad_details.* 
            FROM ad_details
            INNER JOIN id_and_max_time 
            ON ad_details.id = id_and_max_time.id AND ad_details.time = id_and_max_time.time    
        """

        private const val ALL_HISTORY_QUERY = """
            SELECT * 
            FROM ad_details
            WHERE id = :id
            ORDER BY time DESC
        """

        private const val DELETE_BY_ID_QUERY = """
            DELETE FROM ad_details
            WHERE id = :id
        """
    }
}