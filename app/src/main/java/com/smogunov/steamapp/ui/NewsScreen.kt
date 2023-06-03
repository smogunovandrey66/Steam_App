package com.smogunov.steamapp.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.smogunov.steamapp.db.DbNew
import com.smogunov.steamapp.model.MainModel
import com.smogunov.steamapp.utils.format
import java.text.SimpleDateFormat

@Composable
fun NewsScreen(mainModel: MainModel, navController: NavController, appid: Int?) {
    mainModel.setCurrentScreen(SCREEN.NEWS)

    if (appid == null) {
        return
    }

    val stateResultLoad = mainModel.stateResultNews.collectAsStateWithLifecycle()

    LoadingScreen<DbNew>(
        firstLoadFunction = {mainModel.loadNews(appid, false)},
        reloadAllFunction = {mainModel.loadNews(appid, true)},
        stateResultLoad = stateResultLoad
    ) {
        val dateString = it.date.format()
        Text(
            "${it.gid}, ${it.author}, $dateString",
            modifier = Modifier
                .padding(10.dp)
                .border(1.dp, Color.Red)
                .fillMaxWidth()
                .clickable {
                    navController.navigate("${SCREEN.TEXT.name}/${it.gid}")
                }
        )
    }
}