package com.smogunov.steamapp.model

import android.content.Context
import com.smogunov.steamapp.db.SteamAppDatabase
import com.smogunov.steamapp.service.RetrofitClient
import com.smogunov.steamapp.service.SteamService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainModule {

    @Provides
    fun provideRetrofit(): SteamService = RetrofitClient.apiService

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): SteamAppDatabase = SteamAppDatabase.getInstance(context)
}