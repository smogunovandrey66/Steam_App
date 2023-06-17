package com.smogunov.steamapp.model

data class NetApp(
    val appid: UInt,
    val name: String
)

data class NetApps(
    val apps: List<NetApp>
)

data class NetAppList(
    val applist: NetApps
)

data class NetSteamNew(
    val gid: ULong,
    val title: String,
    val is_external_url: Boolean,
    val url: String,
    val author: String,
    val contents: String,
    val date: Long
)

data class AppNews(
    val appid: Int,
    val newsitems: List<NetSteamNew>,
    val count: Int
)

data class NetSteamAppWithNews(
    val appnews: AppNews
)
