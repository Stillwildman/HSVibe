package com.hsvibe.ui.fragments.payment

import android.os.Bundle
import androidx.core.content.ContextCompat
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
class UiPayPasswordFragment private constructor() : BaseActionBarFragment<FragmentPayPasswordBinding>() {

    companion object {
        fun newInstance(isSetNewPassword: Boolean): UiPayPasswordFragment {
            return UiPayPasswordFragment().apply {
                arguments = Bundle(1).also { it.putBoolean(Const.BUNDLE_IS_SET_NEW_PASSWORD, isSetNewPassword) }
            }
        }

        private const val HSPAY_TERMS_HTML = "file:///android_asset/hspay.html"
    }

    override fun getFragmentLayoutId(): Int = R.layout.fragment_pay_password

    override fun getAnimType(): AnimType = AnimType.SlideUp

    override fun getTitleRes(): Int = if (isSetNewPassword) R.string.set_passcode else R.string.transaction_passcode

    override fun getActionBarBackgroundColor(): Int {
        return ContextCompat.getColor(AppController.getAppContext(), R.color.app_background_gradient_top)
    }

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
            viewModel = payPasswordViewModel
            lifecycleOwner = this@UiPayPasswordFragment
        }
    }

    private fun doFirstStep() {
        if (isSetNewPassword || SettingManager.isBiometricVerifyingEnabled().not() || BiometricsHelper.canAuthenticateWithBiometrics().not()) {
            binding.layoutCodeInput.focusAndShowKeyboard()
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
                binding.layoutCodeInput.focusAndShowKeyboard()
            }
        })
    }

    private fun setInputListener() {
        binding.layoutCodeInput.setOnInputChangeListener(object : CodeInputLayout.OnInputChangeListener {
            override fun onComplete(code: String) {
                if (payPasswordViewModel.isFirstInput()) {
                    firstInputCode = code
                    finishFirstStep()
                }
                else {
                    secondInputCode = code
                }
                checkInputValidation()
            }

            override fun onDelete(code: String) {
                if (payPasswordViewModel.isFirstInput()) {
                    firstInputCode = code
                }
                else {
                    secondInputCode = code
                }
                resumeState()
            }
        })
    }

    private fun finishFirstStep() {
        if (isSetNewPassword) {
            payPasswordViewModel.showConfirmationInput(true)
            showConfirmationInput()

            mainViewModel.liveLoadingStatus.observe(viewLifecycleOwner) {
                handleLoadingStatus(it)
            }

            binding.textReadTheTerms.setOnSingleClickListener {
                openTermsWebView()
            }

            binding.buttonConfirm.setOnSingleClickListener {
                updatePayPassword()
            }
        }
        else {
            AppController.instance.hideKeyboard(binding.root)
            verifyPayPassword(firstInputCode)
        }
    }

    private fun showConfirmationInput() {
        binding.textEnterPassword.useFadingAnimation()

        binding.layoutCodeInput.apply {
            clear()
            useFadingAnimation {
                focusAndShowKeyboard()
            }
        }
    }

    private fun checkInputValidation() {
        if (isSetNewPassword) {
            L.i("FirstInputCode: $firstInputCode SecondInputCode: $secondInputCode")
            val isValid = firstInputCode == secondInputCode

            when {
                isValid -> {
                    payPasswordViewModel.setInputLayoutState(CodeInputLayout.STATE_NORMAL)
                    AppController.instance.hideKeyboard(binding.root)
                }
                secondInputCode.isNotEmpty() && isValid.not() -> {
                    payPasswordViewModel.setInputLayoutState(CodeInputLayout.STATE_ERROR)
                }
            }
            payPasswordViewModel.enableButton(isValid)
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

        DialogHelper.showHsVibeDialog(
            getContextSafely(),
            R.style.DialogSurfaceDark,
            R.string.operation_error,
            message,
            R.drawable.ic_close_white,
            R.string.retry
        ) {
            payPasswordViewModel.setInputLayoutState(CodeInputLayout.STATE_NORMAL)
            binding.layoutCodeInput.clear()
            binding.layoutCodeInput.focusAndShowKeyboard()
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

    private fun openTermsWebView() {
        openWebDialogFragment(HSPAY_TERMS_HTML)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (mainViewModel.isPasswordVerified().not()) {
            mainViewModel.setPasswordVerified(false)
        }
    }
}