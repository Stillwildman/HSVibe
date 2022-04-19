package com.hsvibe.ui.fragments.payment

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.callbacks.FingerprintCallback
import com.hsvibe.databinding.FragmentPayPasswordBinding
import com.hsvibe.model.Const
import com.hsvibe.ui.bases.BaseActionBarFragment
import com.hsvibe.utilities.BiometricsHelper
import com.hsvibe.utilities.L
import com.hsvibe.utilities.SettingManager
import com.hsvibe.viewmodel.MainViewModel

/**
 * Created by Vincent on 2022/2/18.
 */
class UiPasswordFragment private constructor() : BaseActionBarFragment<FragmentPayPasswordBinding>() {

    companion object {
        fun newInstance(isSetNewPassword: Boolean): UiPasswordFragment {
            return UiPasswordFragment().apply {
                arguments = Bundle(1).also { it.putBoolean(Const.BUNDLE_IS_SET_NEW_PASSWORD, isSetNewPassword) }
            }
        }
    }

    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun getFragmentLayoutId(): Int = R.layout.fragment_pay_password

    override fun getAnimType(): AnimType = AnimType.SlideUp

    override fun getTitleRes(): Int = if (isSetNewPassword()) R.string.set_passcode else R.string.transaction_passcode

    override fun onInitCompleted() {
        doFirstStep()
    }

    private fun doFirstStep() {
        if (isSetNewPassword() || SettingManager.isBiometricVerifyingEnabled().not() || BiometricsHelper.canAuthenticateWithBiometrics().not()) {
            AppController.instance.showKeyboard(binding.editPassword1)
        }
        else {
            showBiometricsVerification()
        }
    }

    private fun showBiometricsVerification() {
        activity?.let {
            BiometricsHelper.showBiometricPrompt(it, object : FingerprintCallback {
                override fun onVerifyPassed() {
                    L.i("Fingerprint verify success!")
                    mainViewModel.isPasswordVerified = true
                }

                override fun onFailed(errorMessage: String) {
                    L.e(errorMessage)
                    AppController.instance.showKeyboard(binding.editPassword1)
                }
            })
        }
    }

    private fun isSetNewPassword(): Boolean {
        return arguments?.getBoolean(Const.BUNDLE_IS_SET_NEW_PASSWORD, true) == true
    }
}