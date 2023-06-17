package com.smogunov.steamapp.model.mvvm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smogunov.steamapp.db.DbSteamApp
import com.smogunov.steamapp.db.SteamAppDatabase
import com.smogunov.steamapp.model.ResultLoad
import com.smogunov.steamapp.service.SteamService
import com.smogunov.steamapp.ui.SCREEN
import com.smogunov.steamapp.utils.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Модель для работы со списком приложений
 */
@HiltViewModel
class AppsModel @Inject constructor(savedStateHandle: SavedStateHandle) : ViewModel() {

    @Inject
    lateinit var steamNetService: SteamService

    @Inject
    lateinit var steamDataBase: SteamAppDatabase

    private val _currentScreen: MutableStateFlow<SCREEN> = MutableStateFlow(SCREEN.APPS)

    val currentScreen: StateFlow<SCREEN> = _currentScreen

    fun setCurrentScreen(aNewScreen: SCREEN) {
        _currentScreen.value = aNewScreen
    }

    private val _filterApps: MutableStateFlow<String> = MutableStateFlow("")
    val filterApps: StateFlow<String> = _filterApps

    fun setFilterApps(aNewFilter: String) {
        _filterApps.value = aNewFilter
        loadSteamApps(false)
    }

    private val _stateResultSteamApps: MutableStateFlow<ResultLoad> =
        MutableStateFlow(ResultLoad.NotYetLoaded)

    val stateResultSteamApps: StateFlow<ResultLoad> = _stateResultSteamApps

    fun loadSteamApps(clearDb: Boolean) {
        _stateResultSteamApps.value = ResultLoad.Loading
        viewModelScope.launch {
            log("loadSteamApps with clearDb=$clearDb")
            if (clearDb) {
                loadSteamAppsFromNet()
                return@launch
            }

            val apps = steamDataBase.steamAppDao().getAllSteamAppsSuspend("%${filterApps.value}%")
            log("loadSteamApps from database apps.count=${apps.count()}")
            if (apps.isEmpty() && filterApps.value.isEmpty()) {
                loadSteamAppsFromNet()
            } else {
                _stateResultSteamApps.value = ResultLoad.Success(apps)
            }
        }
    }

    /**
     * Загрузка из сети и сохранение в базу
     */
    private suspend fun loadSteamAppsFromNet() {
        try {
            steamDataBase.steamAppDao().clearSteamApps()
            val netApps = steamNetService.getSteamApps()
            log("loadSteamAppsFromNet loaded from net=${netApps.applist.apps.count()}")
            val dublicates = netApps.applist.apps.groupingBy { it.appid }.eachCount()
                .filter { it.value > 1 }.keys
            log("loadSteamAppsFromNet dublicates.count=${dublicates.count()}")

            log("loadSteamAppsFromNet from net netApps.applist.apps.size=${netApps.applist.apps.size}")
            var listDb = netApps.applist.apps
                //Заметил, что Steam Api возвращает дубликаты
                .distinctBy {
                    it.appid
                }
                //Некоторые элементы содержат пустое имя, их пропускаем
                .filter {
                    it.name.trim().isNotEmpty()
                }
//                .filterNot { Исключение китайских символов
//                    it.name.contains(Regex("\\p{IsHan}"))
//                }
                .map {
                    DbSteamApp(it.appid.toInt(), it.name, false)
                }

            log("loadSteamAppsFromNet count items for insert=${listDb.count()}")
            steamDataBase.steamAppDao().insertSteamApps(listDb)
            listDb = listDb.filter {
                it.name.contains(filterApps.value)
            }.sortedBy {
                it.name
            }
            _stateResultSteamApps.value = ResultLoad.Success(listDb)
        } catch (e: Exception) {
            _stateResultSteamApps.value =
                ResultLoad.Error(e.message ?: "Error load steam apps from net")
        }
    }
}