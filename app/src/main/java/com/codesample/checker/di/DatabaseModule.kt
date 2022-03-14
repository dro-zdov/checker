

package com.codesample.checker.di

import android.content.Context
import com.codesample.checker.db.AdDetailsDao
import com.codesample.checker.db.AppDatabase
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context, gson: Gson): AppDatabase {
        return AppDatabase.getInstance(context, gson)
    }

    @Provides
    fun provideAdDetailsDao(appDatabase: AppDatabase): AdDetailsDao {
        return appDatabase.adDetailsDao()
    }

}
