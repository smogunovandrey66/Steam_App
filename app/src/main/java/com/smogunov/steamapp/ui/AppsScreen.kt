package com.smogunov.steamapp.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.smogunov.steamapp.db.DbSteamApp
import com.smogunov.steamapp.model.MainModel

@Composable
fun AppsScreen(mainModel: MainModel, navController: NavController) {

    val resultState = mainModel.stateResultSteamApps.collectAsStateWithLifecycle()
    mainModel.setCurrentScreen(SCREEN.APPS)

    LoadingScreen<DbSteamApp>(
        firstLoadFunction = { mainModel.loadSteamApps(false) },
        reloadAllFunction = { mainModel.loadSteamApps(true) },
        stateResultLoad = resultState
    ) {
        Text(
            "${it.appid} ${it.name}",
            modifier = Modifier
                .padding(10.dp)
                .border(1.dp, Color.Red)
                .fillMaxWidth()
                .clickable {
                    mainModel.setNotLoadedNews()
                    navController.navigate("${SCREEN.NEWS.name}/${it.appid}")
                }
        )
    }
}