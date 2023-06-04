package com.smogunov.steamapp.ui

import android.app.ActionBar.LayoutParams
import android.graphics.Color
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.LinearLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.smogunov.steamapp.model.MainModel
import com.smogunov.steamapp.utils.log


@Composable
fun TextScreen(mainModel: MainModel, gid: Int?) {
    if(gid == null)
        return
    mainModel.setCurrentScreen(SCREEN.TEXT)
    mainModel.setContent(gid)

    val contents by mainModel.stateContents.collectAsStateWithLifecycle()

    if(contents == null)
        return
    if(contents!!.trim().isEmpty())
        return

    contents?.apply {
        TextContent(contents = this)
    }
}
@Composable
private fun TextContent(contents: String) {
//    val webView: WebView = findViewById(R.id.webView)
//
//    val html = "<html><body>" +
//            "<h1>Hello, world!</h1>" +
//            "<p>This is example HTML content with <a href='https://www.google.com'>link to google</a>" +
//            " and image <img src='https://www.example.com/image.jpg'></p>" +
//            "</body></html>"
//
//    webView.loadData(html, "text/html", "UTF-8")
//    return
    log("TextContent with $contents")
    val newContent = contents.replace("<img", "<img width = \"100%\"")
    AndroidView(factory = {
        WebView(it).apply {
            val settings: WebSettings = getSettings()
            settings.builtInZoomControls = true
            settings.setSupportZoom(true)

//            setWebViewClient(object : WebViewClient() {
//                override fun onPageFinished(view: WebView, url: String) {
//                    super.onPageFinished(view, url)
//                    view.loadUrl(
//                        "javascript:(function() { " +
//                                "var imgs = document.getElementsByTagName('img');" +
//                                "for (var i = 0; i < imgs.length; i++) {" +
//                                "    imgs[i].style.maxWidth = '100%';" +
//                                "    imgs[i].style.height = 'auto'" +
//                                "}" +
//                                "})()"
//                    )
//                }
//            })


            layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            setBackgroundColor(Color.parseColor("#FF0000"))
            loadData(newContent, "text/html", "UTF-8")//contents
//            evaluateJavascript(
//                "javascript:(function() { " +
//                        "var imgs = document.getElementsByTagName('img');" +
//                        "for (var i = 0; i < imgs.length; i++) {" +
//                        "    imgs[i].style.maxWidth = '100%';" +
//                        "    imgs[i].style.height = 'auto'" +
//                        "}" +
//                        "})()",
//                null
//            )
        }
    })
}