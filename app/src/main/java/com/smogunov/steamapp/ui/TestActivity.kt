package com.smogunov.steamapp.ui

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.smogunov.steamapp.R
import com.smogunov.steamapp.databinding.ActivityTestBinding
import com.smogunov.steamapp.db.SteamAppDatabase
import com.smogunov.steamapp.utils.log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TestActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityTestBinding

    @Inject
    lateinit var steamDataBase: SteamAppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTestBinding.inflate(layoutInflater)
//        binding.webview.loadUrl("https://www.google.com")
//        val data = "<html><body><h1>Hello World!</h1></body></html>"
//        binding.webview.loadData(data, "text/html", "UTF-8")
        setContentView(binding.root)

        val scoupe = CoroutineScope(Dispatchers.Main)
        scoupe.launch {
            val a = steamDataBase.steamAppDao().getContents(1841377791)

            binding.webview.loadData(a ?: "empty content", "text/html", "UTF-8")
            log(a ?: "null content")
        }
    }
}