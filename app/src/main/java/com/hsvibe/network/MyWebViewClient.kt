package com.hsvibe.network

import android.content.DialogInterface
import android.graphics.Bitmap
import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import com.hsvibe.R
import com.hsvibe.utilities.L

/**
 * Created by Vincent on 2021/6/28.
 */
class MyWebViewClient(private val webCallback: OnWebLoadCallback) : WebViewClient() {

    companion object {
        private const val TAG = "MyWebViewClient"
    }

    interface OnWebLoadCallback {
        fun onLoading()
        fun onFinished()
        fun onBackStackChanged(canGoBack: Boolean)
    }

    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        return super.shouldOverrideUrlLoading(view, request)
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        webCallback.onLoading()
        L.i(TAG, "onPageStarted!! Url: $url")
        super.onPageStarted(view, url, favicon)
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        webCallback.onFinished()
        webCallback.onBackStackChanged(view?.canGoBack() ?: false)
    }

    override fun onLoadResource(view: WebView?, url: String?) {
        super.onLoadResource(view, url)
        view?.apply {
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
        }
    }

    override fun onReceivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        view?.run {
            val clickListener = DialogInterface.OnClickListener { _, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> handler?.proceed()
                    DialogInterface.BUTTON_NEGATIVE -> handler?.cancel()
                }
            }
            
            AlertDialog.Builder(context)
                .setTitle(R.string.ssl_certification_error)
                .setMessage(R.string.ssl_error_confirm_to_continue)
                .setPositiveButton(R.string.go_on_anyway, clickListener)
                .setNegativeButton(R.string.cancel_eng, clickListener)
                .setCancelable(false)
                .create()
                .show()
        }
    }
}