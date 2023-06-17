package com.smogunov.steamapp.model.mvvm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smogunov.steamapp.db.DbNew
import com.smogunov.steamapp.db.SteamAppDatabase
import com.smogunov.steamapp.model.ResultLoad
import com.smogunov.steamapp.service.SteamService
import com.smogunov.steamapp.utils.log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject


/**
 *   Модель для работы с новостями
 */
@HiltViewModel
class NewsModel @Inject constructor(savedStateHandle: SavedStateHandle) : ViewModel() {
    @Inject
    lateinit var steamNetService: SteamService

    @Inject
    lateinit var steamDataBase: SteamAppDatabase

    private val _stateResultNews: MutableStateFlow<ResultLoad> =
        MutableStateFlow(ResultLoad.NotYetLoaded)
    val stateResultNews: StateFlow<ResultLoad> = _stateResultNews

    fun loadNews(appid: Int, clearDb: Boolean) {
        log("loadNews appid=$appid")
        _stateResultNews.value = ResultLoad.Loading
        viewModelScope.launch {
            if (clearDb) {
                loadNewsFromNet(appid)
                return@launch
            }
            var loadedNews = true

            val newsDb = steamDataBase.steamAppDao().getAllNewsSuspend(appid)
            log("loadNews newsDb.count() = ${newsDb.count()}")

            //Если новостей нет
            if (newsDb.isEmpty()) {
                val steamApp = steamDataBase.steamAppDao().getSteamApp(appid)
                //Загружены ли новости
                loadedNews = steamApp.loadedNews
                log("loadNews loadedNews=$loadedNews")
            }

            //Если новости загружены
            if (loadedNews) {
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
}