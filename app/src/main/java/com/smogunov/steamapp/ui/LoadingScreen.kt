package com.smogunov.steamapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.smogunov.steamapp.model.ResultLoad

/**
 *  Параметризованный экран отображения данных
 *  @param firstLoadFunction - функция первоначальной загрузки данных
 *  @param reloadAllFunction - функция для перезагрузки данных
 *  @param stateResultLoad - состояние результата загрузки
 *  @param cardItem - элемент Compose для отображения одного item в LazyColumn
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T> LoadingScreen(
                      firstLoadFunction: () -> Unit,
                      reloadAllFunction: () -> Unit,
                      stateResultLoad: State<ResultLoad>,
                      cardItem: @Composable (T) -> Unit) {

    var refreshing by remember { mutableStateOf(true) }

    val state = rememberPullRefreshState(refreshing, reloadAllFunction)

    Box(
        modifier = Modifier
            .pullRefresh(state)
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val result = stateResultLoad.value
            when (result) {
                ResultLoad.NotYetLoaded -> {
                    firstLoadFunction()
                }
                ResultLoad.Loading -> {
                    refreshing = true
                }
                is ResultLoad.Error -> {
                    item {
                        Box(Modifier.fillParentMaxSize().background(Color.Gray)){
                            Text(result.errorMessage, Modifier.align(Alignment.Center).padding(20.dp))
                        }
                    }
                    refreshing = false
                }
                is ResultLoad.Success<*> -> {
                    val list: List<T> = result.list as List<T>
                    items(list){
                        cardItem(it)
                    }
                    refreshing = false
                }

            }
        }

        PullRefreshIndicator(
            refreshing,
            state,
            Modifier.align(Alignment.Center)
        )
    }
}