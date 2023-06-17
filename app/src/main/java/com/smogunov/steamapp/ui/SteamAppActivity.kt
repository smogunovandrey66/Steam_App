package com.smogunov.steamapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Icon
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.smogunov.steamapp.model.mvvm.AppsModel
import com.smogunov.steamapp.model.mvvm.ContentModel
import com.smogunov.steamapp.model.mvvm.NewsModel
import com.smogunov.steamapp.ui.theme.SteamAppTheme
import com.smogunov.steamapp.utils.log
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SteamAppActivity : ComponentActivity() {

    private val appsModel: AppsModel by viewModels()
    private val newsModel: NewsModel by viewModels()
    private val contentModel: ContentModel by viewModels()

    @OptIn(
        ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SteamAppTheme {
                val navController = rememberNavController()
                val currentBackStackEntry by navController.currentBackStackEntryAsState()
                val textFilterFromModel by appsModel.filterApps.collectAsStateWithLifecycle()
                var textFilterForTextField by remember {
                    mutableStateOf(textFilterFromModel)
                }
                val currentScreen by appsModel.currentScreen.collectAsStateWithLifecycle()

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                when (currentScreen) {
                                    SCREEN.APPS -> {
                                        val keyboardController =
                                            LocalSoftwareKeyboardController.current
                                        //Фильтр поиска
                                        TextField(
                                            value = textFilterForTextField,
                                            onValueChange = {
                                                textFilterForTextField = it
                                            },
                                            leadingIcon = {
                                                Icon(Icons.Default.Search, null,
                                                    modifier = Modifier.clickable {
                                                        keyboardController?.hide()
                                                        appsModel.setFilterApps(
                                                            textFilterForTextField
                                                        )
                                                    }
                                                )
                                            },
                                            trailingIcon = {
                                                Icon(Icons.Default.Clear, null,
                                                    modifier = Modifier.clickable {
                                                        keyboardController?.hide()
                                                        textFilterForTextField = ""
                                                        if (textFilterFromModel.isNotEmpty()) {
                                                            appsModel.setFilterApps("")
                                                        }
                                                    }
                                                )
                                            },
                                            singleLine = true,
                                            keyboardActions = KeyboardActions(
                                                onDone = {
                                                    log("onDone")
                                                    keyboardController?.hide()
                                                    appsModel.setFilterApps(textFilterForTextField)
                                                }
                                            )
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
                                AppsScreen(appsModel, newsModel, navController)
                            }

                            composable(route = "${SCREEN.NEWS.name}/{appid}",
                                arguments = listOf(
                                    navArgument("appid") {
                                        type = NavType.IntType
                                    }
                                )
                            ) {
                                NewsScreen(
                                    newsModel,
                                    appsModel,
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
                                TextScreen(contentModel, appsModel, gid)
                            }
                        }
                    }
                }
            }
        }
    }
}