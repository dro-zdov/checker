package com.codesample.checker.di

import com.codesample.checker.services.AvitoService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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
}