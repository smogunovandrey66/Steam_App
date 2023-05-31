package com.smogunov.steamapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.smogunov.steamapp.model.MainModel
import com.smogunov.steamapp.ui.theme.SteamAppTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainModel: MainModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Twice onCreate in install
        if (savedInstanceState == null)
            mainModel.check()
        setContent {
            SteamAppTheme {
                val navController = rememberNavController()
                val currentBackStackEntry by navController.currentBackStackEntryAsState()

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                when (currentBackStackEntry?.destination?.route) {
                                    SCREEN.APPS.name -> {
                                        var text by remember {
                                            mutableStateOf("")
                                        }
                                        TextField(
                                            value = text,
                                            onValueChange = {
                                                text = it
                                            },
                                            leadingIcon = {
                                                Icon(Icons.Default.Search, null)
                                            },
                                            trailingIcon = {
                                                Icon(Icons.Default.Clear, null)
                                            }
                                        )
                                    }

                                    else -> {
                                        Row {
                                            val nameRoute =
                                                currentBackStackEntry?.destination?.route
                                                    ?: "unknown destination"
                                            Text(text = nameRoute)
                                        }
                                    }
                                }
                            },
                            navigationIcon = {
                                when (currentBackStackEntry?.destination?.route) {
                                    SCREEN.DETAILS.name, SCREEN.TEXT.name -> {
                                        Icon(Icons.Default.ArrowBack, null,
                                            modifier = Modifier
                                                .padding(5.dp)
                                                .clickable {
                                                    navController.popBackStack()
                                                }
                                        )
                                    }
                                }
                            }
                        )
                    }
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = SCREEN.APPS.name
                        ) {
                            composable(SCREEN.APPS.name) {
                                Button(onClick = {
                                    navController.navigate(SCREEN.DETAILS.name)
                                }) {
                                    Text(text = "next")
                                }
                            }

                            composable(SCREEN.DETAILS.name) {
                                Button(onClick = {
                                    navController.navigate(SCREEN.TEXT.name)
                                }) {
                                    Text(text = "next")
                                }
                            }

                            composable(SCREEN.TEXT.name) {
                                Text(text = "end")
                            }
                        }
                    }
                }

                val state = mainModel.stateResultNetwork.collectAsStateWithLifecycle()
//                when (val resultLoad: ResultLoad = state.value) {
//                    ResultLoad.Loading -> {
//                        Box(modifier = Modifier.fillMaxSize()) {
//                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
//                        }
//                    }
//
//                    is ResultLoad.Success -> {
////                        resultLoad.list
////                        Text("Good")
//                        TestPool()
//                    }
//
//                    is ResultLoad.Error -> {
//                        Box(modifier = Modifier
//                            .fillMaxSize()
//                            .padding(20.dp)){
//                            Text(text = resultLoad.errorMessage, modifier = Modifier.align(Alignment.Center))
//                        }
//                    }
//                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TestPool() {
    val refreshScope = rememberCoroutineScope()
    var refreshing by remember { mutableStateOf(true) }
    var itemCount by remember { mutableStateOf(15) }

    fun refresh() = refreshScope.launch {
        refreshing = true
        delay(1500)
        itemCount += 5
        refreshing = false
    }

    LaunchedEffect(null) {
        refreshScope.launch {
            delay(3000)
            refreshing = false
        }
    }

    val state = rememberPullRefreshState(refreshing, ::refresh)


    Box(
        Modifier
            .pullRefresh(state)
            .background(Color.Yellow)
    ) {
        LazyColumn(Modifier.fillMaxSize()) {
            if (!refreshing) {
                items(itemCount) {
                    ListItem { Text(text = "Item ${itemCount - it}") }
                }
            }
        }

        PullRefreshIndicator(refreshing, state, Modifier.align(Alignment.Center))
    }
}