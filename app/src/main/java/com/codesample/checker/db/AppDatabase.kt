package com.codesample.checker.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.codesample.checker.entities.db.AdDetailsContainer
import com.google.gson.Gson

@Database(entities = [AdDetailsContainer::class], version = 1, exportSchema = false)
@TypeConverters(AdDetailsTypeConverters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun adDetailsDao(): AdDetailsDao

    companion object {
        private const val DATABASE_NAME = "ads.db"

        fun getInstance(context: Context, gson: Gson): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                DATABASE_NAME
            )
            .addTypeConverter(AdDetailsTypeConverters(gson))
            .build()
        }
    }
}