package com.smogunov.steamapp.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date

const val GLOBAL_TAG_LOG = "GLOBAL_TAG_LOG"
fun log(aMessage: String){
    Log.d(GLOBAL_TAG_LOG, aMessage)
}

private val simpleDateFormat = SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss")

fun Date.format() = simpleDateFormat.format(this)