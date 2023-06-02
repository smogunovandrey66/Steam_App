package com.smogunov.steamapp.model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.smogunov.steamapp.db.DbSteamApp
import com.smogunov.steamapp.db.SteamAppDatabase
import com.smogunov.steamapp.service.SteamService
import com.smogunov.steamapp.ui.SCREEN
import com.smogunov.steamapp.utils.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ResultLoad {
    class Success(val list: List<DbSteamApp>) : ResultLoad()
    object Loading : ResultLoad()
    class Error(val errorMessage: String) : ResultLoad()
}

@HiltViewModel
class MainModel @Inject constructor(savedStateHandle: SavedStateHandle) : ViewModel() {

    @Inject
    lateinit var steamService: SteamService

    @Inject
    lateinit var dataBase: SteamAppDatabase

    private val _currentScreen: MutableStateFlow<SCREEN> = MutableStateFlow(SCREEN.APPS)
    val currentScreen: StateFlow<SCREEN> = _currentScreen
    fun setCurrentScreen(aNewScreen: SCREEN) {
        _currentScreen.value = aNewScreen
    }

    private val _filterApps: MutableStateFlow<String> = MutableStateFlow("")
    val filterApps: StateFlow<String> = _filterApps

    fun setFilterApps(aNewFilter: String) {
        _filterApps.value = aNewFilter
    }

    fun allSteamApps(aFilter: String) = Pager(PagingConfig(pageSize = 10)){
        dataBase.steamAppDao().getAllSteamAppsFlow("%$aFilter%")
    }.flow.cachedIn(viewModelScope)

    fun check() {
        viewModelScope.launch {
            val apps = dataBase.steamAppDao().getSteamApps(1, 0)
            if (apps.isEmpty()) {
                loadFromNet()
            } else {
                _stateResultNetwork.value =
                    ResultLoad.Success(dataBase.steamAppDao().getSteamApps(10, 0))
            }
        }
    }

    private val _stateResultNetwork: MutableStateFlow<ResultLoad> =
        MutableStateFlow(ResultLoad.Loading)

    val stateResultNetwork: StateFlow<ResultLoad> = _stateResultNetwork

    fun loadFromNet() {
        _stateResultNetwork.value = ResultLoad.Loading
        viewModelScope.launch {
            try {
                dataBase.steamAppDao().clearSteamApps()
                val netApps = steamService.getSteamApps()
                val listDb = netApps.applist.apps.map {
                    DbSteamApp(it.appid.toInt(), it.name, false)
                }
                log("count loaded items=${listDb.count()}")
                dataBase.steamAppDao().insertSteamApps(listDb)
                _stateResultNetwork.value = ResultLoad.Success(listDb)
            } catch (e: Exception) {
                _stateResultNetwork.value = ResultLoad.Error(e.message ?: "Error load from net")
            }
        }
    }

    fun loadNews() {

    }
}