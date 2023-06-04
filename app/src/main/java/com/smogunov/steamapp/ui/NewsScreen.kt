package com.smogunov.steamapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.smogunov.steamapp.db.DbNew
import com.smogunov.steamapp.model.MainModel
import com.smogunov.steamapp.utils.format

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
                    navController.navigate("${SCREEN.TEXT.name}/${it.appid}")
                }
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, top = 5.dp, bottom = 5.dp)
        ) {
            Column {
                Text(
                    it.title,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(
                            LocalTextStyle.current.fontSize.value.dp
                        )
                )
                Row {
                    Text(
                        "Автор: ${it.author}",
                        Modifier
                            .padding(5.dp)
//                            .size(10.dp)
                    )
                    Text(
                        dateString,
                        Modifier
                            .padding(5.dp)
//                            .size(5.dp)
                    )
                }
            }
        }
    }
}