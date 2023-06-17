package com.smogunov.steamapp.model

/**
 * Результат загрузки даннных из модели. Класса Succes параметризован типом.
 */
sealed class ResultLoad {
    class Success<T>(val list: List<T>) : ResultLoad()
    object Loading : ResultLoad()
    object NotYetLoaded : ResultLoad()
    class Error(val errorMessage: String) : ResultLoad()
}