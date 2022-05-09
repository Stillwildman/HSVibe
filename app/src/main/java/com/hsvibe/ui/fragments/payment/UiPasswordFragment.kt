package com.hsvibe.ui.fragments.payment

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.callbacks.FingerprintCallback
import com.hsvibe.databinding.FragmentPayPasswordBinding
import com.hsvibe.model.Const
import com.hsvibe.repositories.PayPasswordRepoImpl
import com.hsvibe.ui.bases.BaseActionBarFragment
import com.hsvibe.utilities.*
import com.hsvibe.viewmodel.MainViewModel
import com.hsvibe.viewmodel.PayPasswordViewModel
import com.hsvibe.viewmodel.PayPasswordViewModelFactory
import com.hsvibe.widgets.CodeInputLayout

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

    override fun getFragmentLayoutId(): Int = R.layout.fragment_pay_password

    override fun getAnimType(): AnimType = AnimType.SlideUp

    override fun getTitleRes(): Int = if (isSetNewPassword) R.string.set_passcode else R.string.transaction_passcode

    private val mainViewModel by activityViewModels<MainViewModel>()

    private val payPasswordViewModel by viewModels<PayPasswordViewModel> { PayPasswordViewModelFactory(PayPasswordRepoImpl()) }

    private val isSetNewPassword by lazy { arguments?.getBoolean(Const.BUNDLE_IS_SET_NEW_PASSWORD, true) == true }

    private var firstInputCode: String = Const.EMPTY_STRING
    private var secondInputCode: String = Const.EMPTY_STRING

    override fun onInitCompleted() {
        bind()
        doFirstStep()
        setInputListener()
        observeLiveData()
    }

    private fun bind() {
        binding.apply {
            isSetNewPassword = this@UiPasswordFragment.isSetNewPassword
            viewModel = payPasswordViewModel
            lifecycleOwner = this@UiPasswordFragment
        }
    }

    private fun doFirstStep() {
        if (isSetNewPassword || SettingManager.isBiometricVerifyingEnabled().not() || BiometricsHelper.canAuthenticateWithBiometrics().not()) {
            binding.layoutCodeInput1.focusAndShowKeyboard()
        }
        else {
            showBiometricsVerification()
        }
    }

    private fun showBiometricsVerification() {
        BiometricsHelper.showBiometricPrompt(this, object : FingerprintCallback {
            override fun onVerifyPassed() {
                L.i("Fingerprint verify success!")
                onPasswordVerified()
            }

            override fun onFailed(errorMessage: String) {
                L.e(errorMessage)
                binding.layoutCodeInput1.focusAndShowKeyboard()
            }
        })
    }

    private fun setInputListener() {
        binding.layoutCodeInput1.setOnInputChangeListener(object : CodeInputLayout.OnInputChangeListener {
            override fun onComplete(code: String) {
                firstInputCode = code
                checkInputValidation()
                finishFirstStep()
            }

            override fun onDelete() {
                resumeState()
            }
        })
    }

    private fun finishFirstStep() {
        if (isSetNewPassword) {
            payPasswordViewModel.showConfirmationInput(true)

            binding.layoutCodeInput2.focusAndShowKeyboard()

            binding.layoutCodeInput2.setOnInputChangeListener(object : CodeInputLayout.OnInputChangeListener {
                override fun onComplete(code: String) {
                    secondInputCode = code
                    checkInputValidation()
                    AppController.instance.hideKeyboard(binding.layoutCodeInput2)
                }

                override fun onDelete() {
                    resumeState()
                }
            })

            mainViewModel.liveLoadingStatus.observe(viewLifecycleOwner) {
                handleLoadingStatus(it)
            }

            binding.buttonConfirm.setOnSingleClickListener {
                updatePayPassword()
            }
        }
        else {
            AppController.instance.hideKeyboard(binding.layoutCodeInput1)
            verifyPayPassword(firstInputCode)
        }
    }

    private fun checkInputValidation() {
        if (isSetNewPassword) {
            val isValid = firstInputCode == secondInputCode
            payPasswordViewModel.enableButton(isValid)

            if (secondInputCode.isNotEmpty() && isValid.not()) {
                payPasswordViewModel.setInputLayoutState(CodeInputLayout.STATE_ERROR)
            }
        }
    }

    private fun resumeState() {
        if (isSetNewPassword) {
            payPasswordViewModel.enableButton(false)
        }
        payPasswordViewModel.setInputLayoutState(CodeInputLayout.STATE_NORMAL)
    }

    private fun observeLiveData() {
        payPasswordViewModel.liveLoadingStatus.observe(viewLifecycleOwner) {
            handleLoadingStatus(it)
        }
        payPasswordViewModel.liveMessage.observe(viewLifecycleOwner) {
            if (it.isSuccess()) {
                onPasswordVerified()
            }
            else {
                showErrorDialog(it.message)
            }
        }
    }

    private fun verifyPayPassword(code: String) {
        payPasswordViewModel.verifyPayPassword(code)
    }

    private fun onPasswordVerified() {
        mainViewModel.setPasswordVerified(true)
        popBack()
    }

    private fun showErrorDialog(message: String) {
        payPasswordViewModel.setInputLayoutState(CodeInputLayout.STATE_ERROR)

        DialogHelper.showSimpleHsVibeDialog(
            getContextSafely(),
            R.string.operation_error,
            message,
            R.drawable.ic_close_white,
            R.string.retry
        ) {
            payPasswordViewModel.setInputLayoutState(CodeInputLayout.STATE_NORMAL)
            binding.layoutCodeInput1.clear()
        }
    }

    private fun updatePayPassword() {
        mainViewModel.updatePayPassword(secondInputCode) { isSuccess ->
            if (isSuccess) {
                Utility.toastShort(R.string.update_success)
                onPasswordVerified()
            } else {
                Utility.toastShort(R.string.update_failed)
            }
        }
    }

    override fun showLoadingCircle() {
        showLoadingDialog()
    }

    override fun hideLoadingCircle() {
        hideLoadingDialog()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (mainViewModel.isPasswordVerified().not()) {
            mainViewModel.setPasswordVerified(false)
        }
    }
}