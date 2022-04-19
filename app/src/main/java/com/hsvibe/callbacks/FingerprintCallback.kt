package com.hsvibe.callbacks

/**
 * Created by Vincent on 2022/2/21.
 */
interface FingerprintCallback {

    fun onVerifyPassed()

    fun onFailed(errorMessage: String)

}