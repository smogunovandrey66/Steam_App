package com.smogunov.steamapp.model.mvvm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.smogunov.steamapp.db.SteamAppDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Модель для контента выбранной новости.
 */
@HiltViewModel
class ContentModel @Inject constructor(savedStateHandle: SavedStateHandle) : ViewModel() {
    @Inject
    lateinit var steamDataBase: SteamAppDatabase

    val _stateContents: MutableStateFlow<String?> = MutableStateFlow("")
    val stateContents: StateFlow<String?> = _stateContents

    fun setContent(gid: Int) {
        viewModelScope.launch {
            _stateContents.value = steamDataBase.steamAppDao().getContents(gid)
        }
    }
}