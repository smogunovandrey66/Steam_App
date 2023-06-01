package com.smogunov.steamapp.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.smogunov.steamapp.db.DbSteamApp
import com.smogunov.steamapp.db.SteamAppDatabase
import com.smogunov.steamapp.service.SteamService
import com.smogunov.steamapp.utils.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

sealed class ResultLoad {
    class Success(val list: List<DbSteamApp>) : ResultLoad()
    object Loading : ResultLoad()
    class Error(val errorMessage: String) : ResultLoad()
}

@HiltViewModel
class MainModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var steamService: SteamService

    @Inject
    lateinit var dataBase: SteamAppDatabase

    private val _filterApps: MutableStateFlow<String> = MutableStateFlow("")
    val filterApps: StateFlow<String> = _filterApps

    fun setFilterApps(aNewFilter: String) {
        _filterApps.value = aNewFilter
    }

    fun allSteamApps(aFilter: String) = Pager(PagingConfig(pageSize = 10)){
        dataBase.streamAppDao().getAllSteamAppsFlow("%$aFilter%")
    }.flow.cachedIn(viewModelScope)

    fun check() {
        viewModelScope.launch {
            val apps = dataBase.streamAppDao().getSteamApps(1, 0)
            if (apps.isEmpty()) {
                loadFromNet()
            } else {
                _stateResultNetwork.value =
                    ResultLoad.Success(dataBase.streamAppDao().getSteamApps(10, 0))
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
                dataBase.streamAppDao().clearSteamApps()
                val netApps = steamService.getSteamApps()
                val listDb = netApps.applist.apps.map {
                    DbSteamApp(it.appid.toInt(), it.name, 0)
                }
                log("count loaded items=${listDb.count()}")
                dataBase.streamAppDao().insertSteamApps(listDb)
                _stateResultNetwork.value = ResultLoad.Success(listDb)
            } catch (e: Exception) {
                _stateResultNetwork.value = ResultLoad.Error(e.message ?: "Error load from net")
            }
        }
    }

    fun loadNews() {

    }
}