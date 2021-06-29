package com.hsvibe.ui.fragments.login

import android.annotation.SuppressLint
import android.view.View
import android.view.Window
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.callbacks.FragmentContract
import com.hsvibe.databinding.FragmentLoginWebViewBinding
import com.hsvibe.network.MyWebViewClient
import com.hsvibe.ui.bases.BaseDialogFragment
import com.hsvibe.utilities.L
import com.hsvibe.viewmodel.LoginViewModel
import com.hsvibe.viewmodel.LoginViewModelFactory
import kotlinx.android.synthetic.main.dialog_loading_circle.*
import kotlinx.android.synthetic.main.fragment_login_web_view.*
import org.json.JSONObject

/**
 * Created by Vincent on 2021/6/28.
 */
class UiLoginWebDialogFragment : BaseDialogFragment<FragmentLoginWebViewBinding>(), MyWebViewClient.OnWebLoadCallback, FragmentContract.ActivityCallback {

    companion object {
        private const val JS_FUNCTION_NAME = "token"
    }

    private val loginViewModel by viewModels<LoginViewModel> { LoginViewModelFactory() }

    override fun getLayoutId(): Int = R.layout.fragment_login_web_view

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
                builtInZoomControls = true
                javaScriptEnabled = true
                loadsImagesAutomatically = true
                allowContentAccess = true
                domStorageEnabled = true
                requestFocus()
                addJavascriptInterface(this@UiLoginWebDialogFragment, JS_FUNCTION_NAME)
            }
            webViewClient = MyWebViewClient(this@UiLoginWebDialogFragment)

            //loadUrl(Urls.WEB_LOGIN)
            loadUrl("https://app.neplus.com.tw/HS_Member/test.html")
        }
    }

    private fun setBackClick() {
        bindingView.buttonBack.setOnClickListener {
             closeOrGoBack()
        }
    }

    @JavascriptInterface
    fun postMessage(obj: Any) {

        L.i("Get Token!!! obj is JSONObject: ${obj is JSONObject}")
        finish()
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
            stopLoading()
            loadUrl("about:blank")
            clearCache(true)
            clearHistory()
        }
        hideLoading()
    }

    private fun closeOrGoBack() {
        bindingView.webView.let {
            if (it.canGoBack()) {
                it.goBack()
            }
            else {
                closeWebView()
            }
        }
    }

    private fun finish() {
        closeWebView()
        dismiss()
    }

    override fun onBackButtonPressed(): Boolean {
        closeOrGoBack()
        return true
    }
}