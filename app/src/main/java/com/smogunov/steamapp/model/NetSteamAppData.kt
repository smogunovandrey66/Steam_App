package com.smogunov.steamapp.model

import java.util.Date

data class NetApp(
    val appid: Int,
    val name: String
)

data class NetApps(
    val apps: List<NetApp>
)

data class NetAppList(
    val applist: NetApps
)

data class NetSteamNew(
    val gid: Int,
    val title: String,
    val url: String,
    val author: String,
    val content: String,
    val date: Date
)

data class NetSteamAppWithNews(
    val appid: Int,
    val news: List<NetSteamNew>,
    val count: Int
)
