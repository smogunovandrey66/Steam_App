package com.smogunov.steamapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.smogunov.steamapp.db.SteamAppDatabase
import com.smogunov.steamapp.model.MainModel
import com.smogunov.steamapp.utils.log
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LoadingScreen(newScreen: SCREEN, model: MainModel, updateOperation: suspend () -> Unit, firstOperation: suspend () -> Unit, content: LazyListScope.() -> Unit) {
    model.setCurrentScreen(newScreen)

    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(true) }

    fun refresh() = refreshScope.launch {
        refreshing = true
        updateOperation()
        refreshing = false
    }

    LaunchedEffect(null) {
//        log("Launch effect one time")
        firstOperation()
        refreshing = false
    }

    val state = rememberPullRefreshState(refreshing, ::refresh)

    Box(
        modifier = Modifier
            .pullRefresh(state)
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray)
        ) {
            if (!refreshing)
                content()
        }

        PullRefreshIndicator(
            refreshing,
            state,
            Modifier.align(Alignment.Center)
        )
    }
}