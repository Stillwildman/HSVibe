package com.hsvibe.utilities

import android.hardware.biometrics.BiometricManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.fragment.app.FragmentActivity
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.callbacks.FingerprintCallback

/**
 * Created by Vincent on 2022/2/18.
 */
object BiometricsHelper {

    @RequiresApi(Build.VERSION_CODES.Q)
    private val availableCodes = listOf(
        BiometricManager.BIOMETRIC_SUCCESS,
        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
    )

    fun canAuthenticateWithBiometrics(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val biometricManager = AppController.getAppContext().getSystemService(BiometricManager::class.java)
            biometricManager?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    availableCodes.contains(biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK))
                }
                else {
                    availableCodes.contains(biometricManager.canAuthenticate())
                }
            } ?: false
        }
        else {
            val fingerprintManagerCompat = FingerprintManagerCompat.from(AppController.getAppContext())
            fingerprintManagerCompat.hasEnrolledFingerprints() && fingerprintManagerCompat.isHardwareDetected
        }
    }

    fun showBiometricPrompt(activity: FragmentActivity, callback: FingerprintCallback) {
        if (canAuthenticateWithBiometrics()) {
            val authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Utility.toastShort(R.string.fingerprint_verify_success)
                    callback.onVerifyPassed()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    errString.toString().let {
                        Utility.toastShort(it)
                        callback.onFailed(it)
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    AppController.getString(R.string.fingerprint_verify_failed).let {
                        Utility.toastShort(it)
                        callback.onFailed(it)
                    }
                }
            }

            val mBiometricPrompt = BiometricPrompt(activity, ContextCompat.getMainExecutor(activity), authenticationCallback)

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(AppController.getString(R.string.password_verification))
                .setDescription(AppController.getString(R.string.fingerprint_input_hint))
                .setNegativeButtonText(AppController.getString(R.string.cancel))
                .build()

            mBiometricPrompt.authenticate(promptInfo)
        }
        else {
            Utility.toastShort(R.string.fingerprint_unavailable)
        }
    }

}