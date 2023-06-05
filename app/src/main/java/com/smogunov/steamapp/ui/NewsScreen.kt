package com.smogunov.steamapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.smogunov.steamapp.db.DbNew
import com.smogunov.steamapp.model.MainModel
import com.smogunov.steamapp.utils.format

/**
 * Экран отображения новостей
 * @param mainModel - модель данных
 * @param navController - контроллер навигации
 * @param appid - идентификатор приложения
 */
@Composable
fun NewsScreen(mainModel: MainModel, navController: NavController, appid: Int?) {
    mainModel.setCurrentScreen(SCREEN.NEWS)

    if (appid == null) {
        return
    }

    val stateResultLoad = mainModel.stateResultNews.collectAsStateWithLifecycle()

    LoadingScreen<DbNew>(
        firstLoadFunction = { mainModel.loadNews(appid, false) },
        reloadAllFunction = { mainModel.loadNews(appid, true) },
        stateResultLoad = stateResultLoad
    ) {
        val dateString = it.date.format()

        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = 5.dp,
            modifier = Modifier
                .clickable {
                    mainModel.setNotLoadedNews()
                    navController.navigate("${SCREEN.TEXT.name}/${it.gid}")
                }
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
        ) {
            Column {
                Text(
                    it.title,
                    modifier = Modifier
                        .padding(10.dp)
                )
                Text(
                    "Автор: ${it.author}",
                    Modifier
                        .padding(5.dp)
                        .align(Alignment.Start),
                    color = Color.LightGray,
                    fontSize = 13.sp
                )
                Text(
                    dateString,
                    modifier = Modifier
                        .padding(5.dp)
                        .align(Alignment.End),
                    fontSize = 13.sp
                )
            }
        }
    }
}