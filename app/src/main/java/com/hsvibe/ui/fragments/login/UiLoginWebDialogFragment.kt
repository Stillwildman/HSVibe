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

//    private fun testGettingToken() {
//        val msgMap = mapOf(
//            "access_token" to "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiI5MjVjMWMzYy02N2NjLTQ3ZmQtYWEwZC01YzcxZWIxYjI3NWIiLCJqdGkiOiI0Yzk0N2ViMmRhMGVkMTE3NzBmMWZmOTgxODFmODc2YjM4NTVmZDRiNzQ2ZDllYjBkMmE0YmUxNzAwMzE3MDg4MWE4YzUwMzdjOGU5YjU3MiIsImlhdCI6MTYyNTA0NzE1My4xMTQyOTYsIm5iZiI6MTYyNTA0NzE1My4xMTQzLCJleHAiOjE2MjU3MzgzNTMuMTAwNzcsInN1YiI6IjQiLCJzY29wZXMiOlsiZ2V0LWJvbnVzIiwiZ2V0LXRpY2tldGhvbGRlciIsImdldC1wcm9maWxlIiwidXBkYXRlLXByb2ZpbGUiXX0.fxBjSe2VQFHe7tGpKXQKcCIJaOvkoOm-P8R9_M7LQ72RMRqiw27MNcApIj1vY9hRezZUfRAeFTuoyFZ2pUlJBtyI_xVUBJFoS9RpiAuuxvlFxNXiyCwHRY1MewSAXU-tLj_S7ylXwB69KGNdrSwVWfWze32ycG1GFJfGITsNubGpbNf9ACU_-6dYVIBluUVWhHs2Z-6sgED17eD_BCsvPCgSAi56jkAvNR9Gs2aX8p23nBKsV8zebFAdq2575XhPX1evAUs-9DIQdAHtfaop0OOpDqxkP1hzAdUrozYwYp29htOSP1K7uEs_fGKbSIpwdyKol--Z17uX0kzGVfQH-_ifMMX-u_2YQ_CgRILEXbOvVsWcHCNIlY7fKLpznAsP34gj5Fwjs-7NfeowCZAfSWxwGIZHNW0S_WnJmtWeJI4aMgR8lolA3TGzYhLxEmbHaR3bMa3uOBCn6xLUX8-axljzlyDrKz2N57ZERhjaJgg3N9_ZFhNTlTIC5IKyyA_8b4E_CxBqMSjQMXxuFbrrYlNo11Uz2sDSP1r1S_PHZtB-RbTLl9DE_jOkDio11zba-agWGEeLzv-n11_H5GAFDi1J0qBqKeV9lYxrQhZw9BYzHwDuKQuBzB2C0b9E5qYc0Z7gZ3kJZHJk0MJMW_hQ2BZVXeAxFUqUKdaILuk8JRE",
//            "refresh_token" to "def50200d67dc5c489f574236ed672f8ea745cffd8e9ecaeac99208b62c10a7b8ec046295908c077265ddba03ff12f856edc0dd38e8856e9b0bdfce63d4fe8bf5208f16eb5f82df1f178dad4e904556727d7efa66f6531cb4d913112232c95796da7a11ce724ff23d69236d9195adc47eefc4063d75d9a3b842517dcf4a92d4015b7ae4eeacdb262a695124b7bc2277a088024d0dc1d2431f60185f95fae13056896da0e3a879a032e83aed0b936ee697c4b08ddcc44fbffcf50c8b600ab3d92ef61c32e471097c0e3a704cb63f6dd94461206096e663be994f24cc8e02372edda771e52fd7b0041184e936bba6e225dd7513b044d8e6132eb54696b3c420e92d45683c2e6f58d4456c8360fcc193645b5c58bc3b63a8fb25de9fe8fa19532a7c4fca018803cbef7974769cce7829d6769b5e9dadafbc4b65a8492b81b824eb8aa26aeb88ed9330715d4f826e668c04bb7f6c0e3eff1984ee82da6611621975268e86d6c6a8712c939596a6ca9a3459da702677bcb8d9ee5001030377f0fd8ad1e9657caa11a3f3b93b19eaa12c80465730514dff9c5dfccc61545ea7da27a4e6e1e9ba225a56ff0578a0e1962cd1c29e7b60a519323535fc7e35c48f16a9ebe2c",
//            "token_type" to "Bearer",
//            "expires_in" to "691200"
//        )
//        val obj = JSONObject(msgMap)
//
//        loginViewModel.updateUserTokenByRawData(obj.toString())
//    }

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