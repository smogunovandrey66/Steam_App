package com.smogunov.steamapp.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.smogunov.steamapp.db.DbNew
import com.smogunov.steamapp.model.MainModel

@Composable
fun NewsScreen(mainModel: MainModel, appid: Int?) {
    if (appid == null)
        return
    var news: List<DbNew> = emptyList()

    LoadingScreen(
        newScreen = SCREEN.NEWS,
        model = mainModel,
        updateOperation = {

        },
        firstOperation = {
            news = mainModel.dataBase.steamAppDao().getNewsSuspend(appid)
            //No news
            if (news.isEmpty()) {
                val steamApp = mainModel.dataBase.steamAppDao().getSteamApp(appid)

                //Yet not loaded
                if (!steamApp.loadedNews) {
                    val netSteamAppWithNews = mainModel.steamService.getNewsSteamApp(appid)
                    news = netSteamAppWithNews.news.map {
                        DbNew(
                            it.gid,
                            appid,
                            it.title,
                            it.url,
                            it.is_external_url,
                            it.author,
                            it.content,
                            it.date
                        )
                    }
                    steamApp.loadedNews = true
                    mainModel.dataBase.steamAppDao().updateSteamApp(steamApp)
                }
            }
        }
    ) {
        items(news.count(), { it -> news[it].gid }) {
            val oneNew = news[it]
            Text(text = "${oneNew.date}")

        }
    }
}