package com.hsvibe.ui.bases

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.hsvibe.network.MyWebViewClient

/**
 * Created by Vincent on 2021/6/28.
 */
abstract class BaseWebFragment<BindingView : ViewDataBinding> : Fragment(), MyWebViewClient.OnWebLoadCallback, View.OnTouchListener {

    protected abstract fun getLayoutId(): Int

    protected abstract fun getLoadingView(): View?

    protected abstract fun getBackButtonView(): AppCompatImageButton?

    protected abstract fun getWebView(): WebView

    protected abstract fun getInitialUrl(): String?

    protected lateinit var bindingView: BindingView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreate(savedInstanceState)
        bindingView = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)

        initWebView()

        return bindingView.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        getWebView().apply {
            settings.run {
                mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                builtInZoomControls = true
                javaScriptEnabled = true
                loadsImagesAutomatically = true
                allowContentAccess = true
                domStorageEnabled = true
                requestFocus()
            }
            webViewClient = MyWebViewClient(this@BaseWebFragment)

            getInitialUrl()?.let { loadUrl(it) }
        }
    }

    override fun onLoading() {
        getLoadingView()?.visibility = View.VISIBLE
    }

    override fun onFinished() {
        getLoadingView()?.visibility = View.GONE
    }

    override fun onBackStackChanged(canGoBack: Boolean) {

    }

    private fun closeWebView() {
        getWebView().run {
            stopLoading()
            loadUrl("about:blank")
            clearCache(true)
            clearHistory()
        }
        getLoadingView()?.visibility = View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.setOnTouchListener(this)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return true
    }
}