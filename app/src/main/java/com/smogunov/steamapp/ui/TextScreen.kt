package com.smogunov.steamapp.ui

import android.app.ActionBar.LayoutParams
import android.webkit.WebView
import android.widget.LinearLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smogunov.steamapp.model.mvvm.AppsModel
import com.smogunov.steamapp.model.mvvm.ContentModel


/**
 * Экран отображения текста
 * @param contentModel - модель данных
 * @param gid - идентификатор новости
 */
@Composable
fun TextScreen(contentModel: ContentModel, appsModel: AppsModel, gid: Int?) {
    if (gid == null)
        return
    appsModel.setCurrentScreen(SCREEN.TEXT)
    contentModel.setContent(gid)

    val contents by contentModel.stateContents.collectAsStateWithLifecycle()

    if (contents == null)
        return
    if (contents!!.trim().isEmpty())
        return

    contents?.apply {
        TextContent(contents = this)
    }
}

@Composable
private fun TextContent(contents: String) {
    //Чтобы изображение не растягивалось
    val newContent = contents.replace("<img", "<img width = \"100%\"")

    //TODO В контенте может присутствовать ссылка на изображение, содержащее STEAM_CLAN_IMAGE - необходимо обрабатывать отдельно

    AndroidView(factory = {
        WebView(it).apply {
            layoutParams =
                LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            loadData(newContent, "text/html", "UTF-8")
        }
    })
}