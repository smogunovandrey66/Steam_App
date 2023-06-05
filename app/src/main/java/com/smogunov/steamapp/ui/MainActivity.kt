package com.smogunov.steamapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.smogunov.steamapp.model.MainModel
import com.smogunov.steamapp.ui.theme.SteamAppTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainModel: MainModel by viewModels()

    @OptIn(
        ExperimentalMaterial3Api::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
                                        Text(text = currentScreen.toAppBarText())
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
                                NewsScreen(
                                    mainModel,
                                    navController,
                                    currentBackStackEntry?.arguments?.getInt("appid")
                                )
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
                                TextScreen(mainModel, gid)
                            }
                        }
                    }
                }
            }
        }
    }
}