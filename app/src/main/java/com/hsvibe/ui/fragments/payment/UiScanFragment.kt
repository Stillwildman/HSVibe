package com.hsvibe.ui.fragments.payment

import android.os.Bundle
import android.view.View
import android.view.Window
import androidx.lifecycle.lifecycleScope
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.google.zxing.Result
import com.hsvibe.R
import com.hsvibe.databinding.FragmentScanningBinding
import com.hsvibe.model.Const
import com.hsvibe.ui.bases.BaseDialogFragment
import com.hsvibe.utilities.Extensions.getQRCodeText
import com.hsvibe.utilities.Extensions.setOnSingleClickListener
import com.hsvibe.utilities.L
import com.hsvibe.utilities.ScannerHelper
import com.hsvibe.utilities.Utility

/**
 * Created by Vincent on 2021/9/9.
 */
class UiScanFragment private constructor() : BaseDialogFragment<FragmentScanningBinding>(), DecodeCallback, ErrorCallback {

    companion object {
        fun newInstance(showOptionButton: Boolean): UiScanFragment {
            return UiScanFragment().apply {
                arguments = Bundle().also { it.putBoolean(Const.BUNDLE_SHOW_OPTION_BUTTON, showOptionButton) }
            }
        }
    }

    private val scanner by lazy { ScannerHelper.createScanner(bindingView.scannerView, this, this) }

    override fun getLayoutId(): Int = R.layout.fragment_scanning

    override fun getAnimType(): AnimType = AnimType.SlideUp

    override fun canCanceledOnTouchOutside(): Boolean = false

    override fun setDialogWindowAttrs(window: Window) {}

    override fun init() {
        showOptionButton()
        setCloseButtonClick()
        Utility.toastShort(R.string.light_hint)
    }

    private fun showOptionButton() {
        if (arguments?.getBoolean(Const.BUNDLE_SHOW_OPTION_BUTTON) == true) {
            bindingView.buttonMyQrCode.visibility = View.VISIBLE
        }
    }

    private fun setCloseButtonClick() {
        bindingView.buttonClose.setOnSingleClickListener {
            dismiss()
        }
    }

    override fun onResume() {
        super.onResume()
        scanner.startPreview()
    }

    override fun onPause() {
        scanner.releaseResources()
        super.onPause()
    }

    override fun onDecoded(result: Result) {
        val code = result.text.getQRCodeText()
        L.i("onDecoded!!! Result: ${result.text}\nCode: $code")
        handleScanResult(code)
    }

    private fun handleScanResult(result: String) {
        lifecycleScope.launchWhenResumed {
            if (result.isNotEmpty() && result.length >= 10) {
                scanner.stopPreview()
                // TODO
            }
        }
    }

    override fun onError(error: Exception) {
        lifecycleScope.launchWhenResumed {
            Utility.toastLong(R.string.check_camera_permission)
            dismiss()
        }
    }

    override fun onDialogBackPressed(): Boolean = false
}