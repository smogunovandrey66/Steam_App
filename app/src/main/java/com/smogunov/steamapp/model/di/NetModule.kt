package com.smogunov.steamapp.model.di

import com.smogunov.steamapp.service.RetrofitClient
import com.smogunov.steamapp.service.SteamService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object NetModule {
    @Provides
    fun provideRetrofit(): SteamService = RetrofitClient.apiService
}