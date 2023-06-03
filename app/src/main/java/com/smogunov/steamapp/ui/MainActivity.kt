package com.smogunov.steamapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.smogunov.steamapp.model.MainModel
import com.smogunov.steamapp.ui.theme.SteamAppTheme
import com.smogunov.steamapp.utils.log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainModel: MainModel by viewModels()

    @OptIn(
        ExperimentalMaterial3Api::class,
        ExperimentalMaterialApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Twice onCreate in install
//        if (savedInstanceState == null)
//            mainModel.check()
        setContent {
            SteamAppTheme {
                val navController = rememberNavController()
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val textFilter by mainModel.filterApps.collectAsStateWithLifecycle()
                val currentScreen by mainModel.currentScreen.collectAsStateWithLifecycle()

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                when (currentScreen) {
                                    SCREEN.APPS -> {
                                        TextField(
                                            value = textFilter,
                                            onValueChange = {
                                                mainModel.setFilterApps(it)
                                            },
                                            trailingIcon = {
                                                Icon(Icons.Default.Clear, null,
                                                    modifier = Modifier.clickable {
                                                        mainModel.setFilterApps("")
                                                    }
                                                )
                                            }
                                        )
                                    }
                                    else -> {
                                        Text(text = currentScreen.name)
                                    }
                                }
                            },

                            navigationIcon = {
                                when (currentScreen) {
                                    SCREEN.NEWS, SCREEN.TEXT -> {
                                        Icon(Icons.Default.ArrowBack, null,
                                            modifier = Modifier
                                                .padding(5.dp)
                                                .clickable {
                                                    navController.popBackStack()
                                                }
                                        )
                                    }
                                    else -> {

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
                                AppsScreen(mainModel, navController)
                            }

                            composable(route = "${SCREEN.NEWS.name}/{appid}",
                                arguments = listOf(
                                    navArgument("appid") {
                                        type = NavType.IntType
                                    }
                                )
                            ) {
                                NewsScreen(mainModel, navController, currentBackStackEntry?.arguments?.getInt("appid"))
                            }

                            composable(
                                route = "${SCREEN.TEXT.name}/{gid}",
                                arguments = listOf(
                                    navArgument("gid") {
                                        type = NavType.IntType
                                    }
                                )
                            ) {
                                val gid = currentBackStackEntry?.arguments?.getInt("gid")

                                log("appid in navigation  in text = $gid")

//                                Text("gid=$gid")
//                                mainModel.setCurrentScreen(SCREEN.TEXT)

                                TextScreen(mainModel, gid)
                            }
                        }
                    }
                }

//                val state = mainModel.stateResultSteamApps.collectAsStateWithLifecycle()
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
    var refreshing by remember { mutableStateOf(false) }
    var itemCount by remember { mutableIntStateOf(15) }

    fun refresh() = refreshScope.launch {
        refreshing = true
        delay(1500)
        itemCount += 5
        refreshing = false
    }

//    LaunchedEffect(null) {
//        refreshScope.launch {
//            delay(3000)
//            refreshing = false
//        }
//    }

    val state = rememberPullRefreshState(refreshing, ::refresh)

    Box(
        Modifier
            .background(Color.Yellow)
            .fillMaxSize()
            .pullRefresh(state)
    ) {
        LazyColumn(Modifier.fillMaxSize()) {
            if (!refreshing) {
                items(itemCount) {
                    ListItem { Text(text = "Item ${itemCount - it}") }
                }
            }
        }
//        Column(Modifier.fillMaxSize()) {
//            Text("1")
//            Text("2")
//        }
//        Text("txt")

        PullRefreshIndicator(refreshing, state, Modifier.align(Alignment.Center))
    }
}