package com.codesample.checker.di

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class JsonModule {

    @Provides
    fun provideGson(): Gson = Gson()
}
