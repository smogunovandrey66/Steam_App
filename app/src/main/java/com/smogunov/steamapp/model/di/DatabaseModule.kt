package com.smogunov.steamapp.model.di

import android.content.Context
import com.smogunov.steamapp.db.SteamAppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): SteamAppDatabase =
        SteamAppDatabase.getInstance(context)
}