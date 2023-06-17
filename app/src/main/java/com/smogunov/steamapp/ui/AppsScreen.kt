package com.smogunov.steamapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.smogunov.steamapp.db.DbSteamApp
import com.smogunov.steamapp.model.mvvm.AppsModel
import com.smogunov.steamapp.model.mvvm.NewsModel

/**
 * Экран списка приложений
 * @param appsModel - модель данных
 * @param navController - контроллер навигации
 */
@Composable
fun AppsScreen(appsModel: AppsModel, newsModel: NewsModel, navController: NavController) {

    val resultState = appsModel.stateResultSteamApps.collectAsStateWithLifecycle()
    appsModel.setCurrentScreen(SCREEN.APPS)

    LoadingScreen<DbSteamApp>(
        firstLoadFunction = { appsModel.loadSteamApps(false) },
        reloadAllFunction = { appsModel.loadSteamApps(true) },
        stateResultLoad = resultState
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = 5.dp,
            modifier = Modifier
                .clickable {
                    newsModel.setNotLoadedNews()
                    navController.navigate("${SCREEN.NEWS.name}/${it.appid}")
                }
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
        ) {
            Text(
                it.name,
                modifier = Modifier
                    .padding(10.dp)
            )
        }
    }
}