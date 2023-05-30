package com.smogunov.steamapp.ui

import android.os.Bundle
import android.widget.ProgressBar
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smogunov.steamapp.model.MainModel
import com.smogunov.steamapp.model.ResultLoad
import com.smogunov.steamapp.ui.theme.SteamAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SteamAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val mainModel: MainModel = hiltViewModel()
                    mainModel.check()
                    val state = mainModel.stateResultNetwork.collectAsStateWithLifecycle()
                    when (state.value) {
                        ResultLoad.Loading -> {
                            CircularProgressIndicator()
                        }
                        is ResultLoad.Success -> {
                            (state.value as ResultLoad.Success).list
                            Text("Good")
                        }
                        is ResultLoad.Error -> {
                            Text(text = "Error")
                        }
                    }
//
//                    mainModel.steamService
//                    TextButton(onClick = {
//                        coroutineScope.launch {
//                            val d = mainModel.dataBase.streamAppDao().getSteamApps(1, 2)
//                            Log.d("MainActivity_TAG", Calendar.getInstance().time.toString())
//                            val netAppList = mainModel.steamService.getSteamApps()
//                            Log.d("MainActivity_TAG", Calendar.getInstance().time.toString())
//                            Log.d("MainActivity_TAG", netAppList.applist.apps.count().toString())
//                                Log.d("MainActivity_TAG", netAppList.applist.apps.toString())
//                        }
//                    }) {
//                        Text(text = "Click me")
//                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SteamAppTheme {
        Greeting("Android")
    }
}