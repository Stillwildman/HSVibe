package com.hsvibe.ui.fragments.banner

import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import com.hsvibe.R
import com.hsvibe.databinding.FragmentBannerWebViewBinding
import com.hsvibe.model.Const
import com.hsvibe.ui.bases.BaseWebFragment

/**
 * Created by Vincent on 2021/8/2.
 */
class UiBannerWebFragment private constructor() : BaseWebFragment<FragmentBannerWebViewBinding>() {

    companion object {
        fun newInstance(webUrl: String): UiBannerWebFragment {
            return UiBannerWebFragment().apply {
                arguments = Bundle(1).also { it.putString(Const.BUNDLE_WEB_URL, webUrl) }
                childFragmentManager
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.fragment_banner_web_view

    override fun getAnimType(): AnimType = AnimType.SlideUp

    override fun getLoadingView(): View = bindingView.loadingCircle

    override fun getBackButtonView(): AppCompatImageView = bindingView.buttonBack

    override fun getRefreshButtonView(): AppCompatImageView = bindingView.buttonRefresh

    override fun getTitleView(): TextView = bindingView.textHeaderTitle

    override fun getWebView(): WebView = bindingView.webView

    override fun onInitializing(webView: WebView) {}

    override fun getInitialUrl(): String? = arguments?.getString(Const.BUNDLE_WEB_URL)
}