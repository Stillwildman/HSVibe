package com.hsvibe.ui.fragments.payment

import android.os.Bundle
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.hsvibe.R
import com.hsvibe.databinding.FragmentBasicWebViewBinding
import com.hsvibe.model.Const
import com.hsvibe.model.Urls
import com.hsvibe.ui.bases.BaseWebFragment
import com.hsvibe.utilities.L
import com.hsvibe.utilities.Utility
import java.text.MessageFormat

/**
 * Created by Vincent on 2022/5/10.
 */
class UiBindCardWebFragment private constructor() : BaseWebFragment<FragmentBasicWebViewBinding>() {

    companion object {
        fun newInstance(webUrl: String): UiBindCardWebFragment {
            return UiBindCardWebFragment().apply {
                arguments = Bundle(1).also { it.putString(Const.BUNDLE_UUID, webUrl) }
            }
        }

        private const val JS_FUNCTION_NAME = "payment_callback"
    }

    override fun getLayoutId(): Int = R.layout.fragment_basic_web_view

    override fun getAnimType(): AnimType = AnimType.SlideUp

    override fun getLoadingView(): View = bindingView.loadingCircle

    override fun getBackButtonView(): AppCompatImageView = bindingView.buttonBack

    override fun getRefreshButtonView(): AppCompatImageView = bindingView.buttonRefresh

    override fun getTitleView(): TextView = bindingView.textHeaderTitle

    override fun getWebView(): WebView = bindingView.webView

    override fun onInitializing(webView: WebView) {
        webView.addJavascriptInterface(this, JS_FUNCTION_NAME)
    }

    override fun getHeaders(): Map<String, String>? = null

    override fun getInitialUrl(): String? {
        return arguments?.getString(Const.BUNDLE_UUID)?.let { uuid ->
            MessageFormat.format(Urls.WEB_REGISTER_CREDIT_CARD, uuid)
        }
    }

    @JavascriptInterface
    fun postMessage(msg: String?) {
        L.i("Get CreditCardCallback!!! msg: $msg")
        Utility.toastShort("DONE!")
    }
}