package com.codesample.checker.entities.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.codesample.checker.entities.details.AdDetails

@Entity(tableName = "ad_details")
data class AdDetailsContainer(
    @PrimaryKey(autoGenerate = true) val rowId: Int? = null,
    @Embedded val details: AdDetails,
)