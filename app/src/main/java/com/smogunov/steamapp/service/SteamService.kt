package com.smogunov.steamapp.service

import com.smogunov.steamapp.model.NetAppList
import com.smogunov.steamapp.model.NetSteamAppWithNews
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface SteamService {
    /**
     * Получение приложений
     */
    @GET("ISteamApps/GetAppList/v2")
    suspend fun getSteamApps(): NetAppList

    /**
     * Получение новостей
     */
    @GET("ISteamNews/GetNewsForApp/v2")
    suspend fun getNewsSteamApp(@Query("appid") appid: Int): NetSteamAppWithNews?
}

object RetrofitClient{
    const val BASE_URL = "https://api.steampowered.com/"

    fun getClient(): Retrofit{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: SteamService = getClient().create(SteamService::class.java)
}