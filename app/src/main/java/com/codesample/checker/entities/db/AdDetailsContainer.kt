package com.codesample.checker.entities.db

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.codesample.checker.entities.details.AdDetails
import java.io.File

@Entity(tableName = "ad_details")
data class AdDetailsContainer(
    @PrimaryKey(autoGenerate = true) val rowId: Int? = null,
    // Workaround: without nullability here room adds NOT NULL to all fields inside AdDetails,
    // but we actually need some fields inside to be nullable
    @Embedded val details_internal: AdDetails?,
    val files: List<File>
) {
    constructor(details: AdDetails) : this(null, details, listOf())
    constructor(details: AdDetails, files: List<File>) : this(null, details, files)

    val details: AdDetails get() = details_internal!!
}