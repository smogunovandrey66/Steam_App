package com.smogunov.steamapp.model

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smogunov.steamapp.db.DbNew
import com.smogunov.steamapp.db.DbSteamApp
import com.smogunov.steamapp.db.SteamAppDatabase
import com.smogunov.steamapp.service.SteamService
import com.smogunov.steamapp.ui.SCREEN
import com.smogunov.steamapp.utils.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * Результат загрузки даннных из модели. Класса Succes параметризован типом.
 */
sealed class ResultLoad {
    class Success<T>(val list: List<T>) : ResultLoad()
    object Loading : ResultLoad()
    object NotYetLoaded : ResultLoad()
    class Error(val errorMessage: String) : ResultLoad()
}

/**
 * Основная модель
 */
@HiltViewModel
class MainModel @Inject constructor(savedStateHandle: SavedStateHandle) : ViewModel() {

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
            val dublicates = netApps.applist.apps.groupingBy { it.appid }.eachCount().filter { it.value > 1 }.keys
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
            _stateResultSteamApps.value = ResultLoad.Error(e.message ?: "Error load steam apps from net")
        }
    }

    private val _stateResultNews: MutableStateFlow<ResultLoad> =
        MutableStateFlow(ResultLoad.NotYetLoaded)
    val stateResultNews: StateFlow<ResultLoad> = _stateResultNews

    fun loadNews(appid: Int, clearDb: Boolean) {
        log("loadNews appid=$appid")
        _stateResultNews.value = ResultLoad.Loading
        viewModelScope.launch {
            if(clearDb){
                loadNewsFromNet(appid)
                return@launch
            }
            var loadedNews = true

            val newsDb = steamDataBase.steamAppDao().getAllNewsSuspend(appid)
            log("loadNews newsDb.count() = ${newsDb.count()}")

            //Если новостей нет
            if(newsDb.isEmpty()) {
                val steamApp = steamDataBase.steamAppDao().getSteamApp(appid)
                //Загружены ли новости
                loadedNews = steamApp.loadedNews
                log("loadNews loadedNews=$loadedNews")
            }

            //Если новости загружены
            if(loadedNews) {
                _stateResultNews.value = ResultLoad.Success(newsDb)
            } else {
                //Загружаем из сети
                loadNewsFromNet(appid)
            }
        }
    }

    private suspend fun loadNewsFromNet(appid: Int) {
        try {
            //Очищаем новости для выбранного приложения
            steamDataBase.steamAppDao().clearNews(appid)
            val netNews = steamNetService.getNewsSteamApp(appid)?.appnews?.newsitems ?: emptyList()
            val dbNews = netNews.map {
                DbNew(
                    it.gid.toInt(),
                    appid,
                    it.title,
                    it.url,
                    it.is_external_url,
                    it.author,
                    it.contents,
                    Date(it.date * 1000)
                )
            }
            log("loadNewsFromNet insert news")
            steamDataBase.steamAppDao().insertNews(dbNews)
            _stateResultNews.value = ResultLoad.Success(dbNews)

            //Устанавливаем признак, что новости уже загрузили. Чтобы лишинй раз не обращаться в сеть.
            val app = steamDataBase.steamAppDao().getSteamApp(appid)
            app.loadedNews = true
            steamDataBase.steamAppDao().updateSteamApp(app)
        } catch (e: Exception) {
            log("loadNewsFromNet exception ${e.message}")
            _stateResultNews.value = ResultLoad.Error(e.message ?: "Error load news from net")
        }
    }

    fun setNotLoadedNews() {
        _stateResultNews.value = ResultLoad.NotYetLoaded
    }

    val _stateContents: MutableStateFlow<String?> = MutableStateFlow("")
    val stateContents: StateFlow<String?> = _stateContents

    fun setContent(gid: Int) {
        viewModelScope.launch {
            _stateContents.value = steamDataBase.steamAppDao().getContents(gid)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.N)
fun main() {
    val list = listOf(1, 2, 3, 1)
    val a = list.groupingBy { it }.eachCount().filter { it.value > 1 }.keys
    println(a)
}