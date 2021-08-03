package com.hsvibe.ui.bases

import android.annotation.SuppressLint
import android.view.*
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.network.MyWebChromeClient
import com.hsvibe.network.MyWebViewClient
import com.hsvibe.utilities.L

/**
 * Created by Vincent on 2021/6/28.
 */
abstract class BaseWebFragment<BindingView : ViewDataBinding> : BaseDialogFragment<BindingView>(), MyWebViewClient.OnWebLoadCallback {

    protected abstract fun getLoadingView(): View?

    protected abstract fun getBackButtonView(): AppCompatImageView?

    protected abstract fun getRefreshButtonView(): AppCompatImageView?

    protected abstract fun getTitleView(): TextView?

    protected abstract fun getWebView(): WebView

    protected abstract fun onInitializing(webView: WebView)

    protected abstract fun getInitialUrl(): String?

    override fun canCanceledOnTouchOutside(): Boolean = false

    override fun setDialogWindowAttrs(window: Window) {

    }

    override fun init() {
        initWebView()
        setButtonsClick()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        getWebView().also { webView ->
            webView.settings.run {
                mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                builtInZoomControls = true
                javaScriptEnabled = true
                loadsImagesAutomatically = true
                allowContentAccess = true
                domStorageEnabled = true
            }
            webView.requestFocus()
            webView.webChromeClient = object : MyWebChromeClient() {
                override fun onReceivedTitle(view: WebView, title: String?) {
                    title?.let { getTitleView()?.text = it }
                }
            }
            webView.webViewClient = MyWebViewClient(this@BaseWebFragment)

            onInitializing(webView)
            getInitialUrl()?.let {
                webView.loadUrl(it)
                L.i("DialogFragment init duration: ${(System.nanoTime() - startTime) / 1000000}")
            }
        }
    }

    private fun setButtonsClick() {
        getBackButtonView()?.setOnClickListener {
            closeOrGoBack()
        }
        getRefreshButtonView()?.setOnClickListener {
            getWebView().reload()
        }
    }

    protected fun showLoading() {
        getLoadingView()?.visibility = View.VISIBLE
    }

    protected fun hideLoading() {
        getLoadingView()?.visibility = View.GONE
    }

    override fun onLoading() {
        showLoading()
    }

    override fun onFinished() {
        hideLoading()
    }

    override fun onBackStackChanged(canGoBack: Boolean) {
        val buttonIconRes = if (canGoBack) R.drawable.ic_back_arrow_light else R.drawable.ic_close_light
        getBackButtonView()?.setImageDrawable(ContextCompat.getDrawable(AppController.getAppContext(), buttonIconRes))
    }

    override fun onDialogBackPressed(): Boolean {
        closeOrGoBack()
        return true
    }

    private fun closeWebView() {
        getWebView().run {
            post {
                //loadUrl("about:blank")
                stopLoading()
                clearCache(true)
                clearHistory()
                hideLoading()
            }
        }
    }

    private fun closeOrGoBack() {
        getWebView().let {
            if (it.canGoBack()) {
                it.goBack()
            }
            else {
                finish()
            }
        }
    }

    private fun finish() {
        closeWebView()
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        closeWebView()
    }
}