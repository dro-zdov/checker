package com.codesample.checker.di

import android.content.Context
import com.codesample.checker.R
import com.codesample.checker.services.AvitoService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideAvitoService(): AvitoService {
        return AvitoService.create()
    }

    @Singleton
    @Provides
    fun provideAvitoKey(@ApplicationContext context: Context): String {
        return context.getString(R.string.avito_key)
    }
}