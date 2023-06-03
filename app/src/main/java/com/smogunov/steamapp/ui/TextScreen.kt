package com.smogunov.steamapp.ui

import android.webkit.WebView
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smogunov.steamapp.model.MainModel
import com.smogunov.steamapp.utils.log

@Composable
fun TextScreen(mainModel: MainModel, gid: Int?) {
    if(gid == null)
        return
    mainModel.setContent(gid)

    val contents by mainModel.stateContents.collectAsStateWithLifecycle()
    TextContent(contents)
}
@Composable
private fun TextContent(contents: String) {
    log("TextContent with $contents")
    AndroidView(factory = {
        TextView(it).apply {

            this.text = HtmlCompat.fromHtml(contents, HtmlCompat.FROM_HTML_MODE_LEGACY)
        }
    })
}