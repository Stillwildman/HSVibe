package com.hsvibe.ui.fragments.login

import android.annotation.SuppressLint
import android.view.View
import android.view.Window
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.databinding.FragmentLoginWebViewBinding
import com.hsvibe.model.Urls
import com.hsvibe.network.MyWebChromeClient
import com.hsvibe.network.MyWebViewClient
import com.hsvibe.ui.bases.BaseDialogFragment
import com.hsvibe.utilities.L
import com.hsvibe.viewmodel.LoginViewModel
import com.hsvibe.viewmodel.LoginViewModelFactory

/**
 * Created by Vincent on 2021/6/28.
 */
class UiLoginWebDialogFragment : BaseDialogFragment<FragmentLoginWebViewBinding>(), MyWebViewClient.OnWebLoadCallback {

    companion object {
        private const val JS_FUNCTION_NAME = "token"
    }

    private val loginViewModel by activityViewModels<LoginViewModel> { LoginViewModelFactory() }

    override fun getLayoutId(): Int = R.layout.fragment_login_web_view

    override fun useSlideUpAnim(): Boolean = true

    override fun canCanceledOnTouchOutside(): Boolean = false

    override fun setDialogWindowAttrs(window: Window) {

    }

    override fun init() {
        initWebView()
        setBackClick()
    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    private fun initWebView() {
        bindingView.webView.apply {
            settings.run {
                mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                builtInZoomControls = false
                javaScriptEnabled = true
                loadsImagesAutomatically = true
                allowContentAccess = true
                domStorageEnabled = true
                requestFocus()
            }
            setBackgroundColor(ContextCompat.getColor(AppController.getAppContext(), R.color.transparent_half_black))
            addJavascriptInterface(this@UiLoginWebDialogFragment, JS_FUNCTION_NAME)

            webChromeClient = MyWebChromeClient()
            webViewClient = MyWebViewClient(this@UiLoginWebDialogFragment)

            loadUrl(Urls.WEB_LOGIN)
            //loadUrl("https://stg-oauth.hsvibe.com/dashboard")
        }
    }

    private fun setBackClick() {
        bindingView.buttonBack.setOnClickListener {
             closeOrGoBack()
        }
    }

    @JavascriptInterface
    fun postMessage(msg: String?) {
        L.i("Get Token!!! msg: $msg")
        parseUserTokenAndUpdate(msg)
    }

    private fun parseUserTokenAndUpdate(msg: String?) {
        msg?.let { loginViewModel.updateUserTokenByRawData(it) }
    }

    private fun showLoading() {
        bindingView.loadingCircle.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        bindingView.loadingCircle.visibility = View.GONE
    }

    override fun onLoading() {
        showLoading()
    }

    override fun onFinished() {
        hideLoading()
    }

    override fun onBackStackChanged(canGoBack: Boolean) {
        val buttonIconRes = if (canGoBack) R.drawable.ic_back_arrow_light else R.drawable.ic_close_light
        bindingView.buttonBack.setImageDrawable(ContextCompat.getDrawable(AppController.getAppContext(), buttonIconRes))
    }

    private fun closeWebView() {
        bindingView.webView.run {
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
        bindingView.webView.let {
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