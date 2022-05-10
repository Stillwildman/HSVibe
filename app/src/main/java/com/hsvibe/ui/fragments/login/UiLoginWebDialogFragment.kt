package com.hsvibe.ui.fragments.login

import android.view.View
import android.view.Window
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.databinding.FragmentWebViewBinding
import com.hsvibe.model.Urls
import com.hsvibe.network.MyWebViewClient
import com.hsvibe.ui.bases.BaseWebFragment
import com.hsvibe.utilities.L
import com.hsvibe.viewmodel.LoginViewModel

/**
 * Created by Vincent on 2021/6/28.
 */
class UiLoginWebDialogFragment : BaseWebFragment<FragmentWebViewBinding>(), MyWebViewClient.OnWebLoadCallback {

    companion object {
        private const val JS_FUNCTION_NAME = "token"
    }

    private val loginViewModel by activityViewModels<LoginViewModel>()

    override fun getLayoutId(): Int = R.layout.fragment_web_view

    override fun getAnimType(): AnimType = AnimType.SlideUp

    override fun canCanceledOnTouchOutside(): Boolean = false

    override fun setDialogWindowAttrs(window: Window) {}

    override fun getLoadingView(): View = bindingView.loadingCircle

    override fun getBackButtonView(): AppCompatImageView = bindingView.buttonBack

    override fun getRefreshButtonView(): AppCompatImageView? = null

    override fun getTitleView(): TextView? = null

    override fun getWebView(): WebView = bindingView.webView

    override fun onInitializing(webView: WebView) {
        webView.apply {
            setBackgroundColor(ContextCompat.getColor(AppController.getAppContext(), R.color.md_grey_900))
            addJavascriptInterface(this@UiLoginWebDialogFragment, JS_FUNCTION_NAME)
        }
    }

    override fun getHeaders(): Map<String, String>? = null

    override fun getInitialUrl(): String = Urls.WEB_LOGIN //Logout test: https://stg-oauth.hsvibe.com/dashboard

    @JavascriptInterface
    fun postMessage(msg: String?) {
        L.i("Get Token!!! msg: $msg")
        parseUserTokenAndUpdate(msg)
    }

    private fun parseUserTokenAndUpdate(msg: String?) {
        msg?.let { loginViewModel.updateUserTokenByRawData(it) }
    }
}