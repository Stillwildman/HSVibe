package com.hsvibe.ui.fragments.login

import android.view.Window
import com.hsvibe.R
import com.hsvibe.databinding.DialogLoadingCircleBinding
import com.hsvibe.ui.bases.BaseDialogFragment

/**
 * Created by Vincent on 2021/6/28.
 */
class UiLoadingDialogFragment(private val loadingTextRes: Int? = null) : BaseDialogFragment<DialogLoadingCircleBinding>() {

    override fun getLayoutId(): Int = R.layout.dialog_loading_circle

    override fun canCanceledOnTouchOutside(): Boolean = false

    override fun setDialogWindowAttrs(window: Window) {

    }

    override fun init() {
        loadingTextRes?.let { bindingView.loadingTextView.setText(it) }
    }
}