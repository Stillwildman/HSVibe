package com.hsvibe.network

import android.content.DialogInterface
import android.webkit.JsPromptResult
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import com.hsvibe.R

/**
 * Created by vincent.chang on 2018/1/23.
 */
class MyWebChromeClient : WebChromeClient() {
    override fun onJsConfirm(view: WebView, url: String, message: String, result: JsResult): Boolean {
        AlertDialog.Builder(view.context, R.style.Theme_AppCompat_Light_Dialog).apply {
            setTitle(R.string.confirm)
            setMessage(message)
            setPositiveButton(android.R.string.ok) { _: DialogInterface?, _: Int ->
                result.confirm()
            }
            setNegativeButton(android.R.string.cancel) { _: DialogInterface?, _: Int ->
                result.cancel()
            }
        }.create().show()
        return true
    }

    override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
        AlertDialog.Builder(view.context, R.style.Theme_AppCompat_Light_Dialog).apply {
            setTitle(R.string.confirm)
            setMessage(message)
            setPositiveButton(android.R.string.ok) { _: DialogInterface?, _: Int ->
                result.confirm()
            }
            setNegativeButton(android.R.string.cancel) { _: DialogInterface?, _: Int ->
                result.cancel()
            }
        }.create().show()
        return true
    }

    override fun onJsPrompt(view: WebView, url: String, message: String, defaultValue: String, result: JsPromptResult): Boolean {
        AlertDialog.Builder(view.context, R.style.Theme_AppCompat_Light_Dialog).apply {
            setTitle(R.string.confirm)
            setMessage(message)
            setPositiveButton(android.R.string.ok) { _: DialogInterface?, _: Int ->
                result.confirm()
            }
            setNegativeButton(android.R.string.cancel) { _: DialogInterface?, _: Int ->
                result.cancel()
            }
        }.create().show()
        return true
    }
}