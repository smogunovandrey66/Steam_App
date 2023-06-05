package com.smogunov.steamapp.ui

/**
 * Перечисления экранов
 */
enum class SCREEN {
    APPS,
    NEWS,
    TEXT
}

fun SCREEN.toAppBarText() = when(this) {
        SCREEN.APPS -> "Приложения"
        SCREEN.TEXT -> "Контент"
        SCREEN.NEWS -> "Новости"
    }