package com.hsvibe.ui

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.hsvibe.R
import com.hsvibe.model.UserTokenManager
import com.hsvibe.ui.bases.BaseFullScreenActivity
import com.hsvibe.utilities.L
import com.hsvibe.utilities.startActivitySafely

class UiSplashActivity : BaseFullScreenActivity() {

    override fun getLayoutId(): Int = R.layout.activity_splash

    override fun init() {
        checkGooglePlayService()
    }

    private fun checkGooglePlayService() {
        if (checkPlayServices()) {
            requireLocationPermission()
        }
        else {
            // Continue without Location permission.
            checkIfTheUserHasToken()
        }
    }

    override fun onPermissionGranted(requestCode: Int) {
        L.i(TAG, "onPermissionGranted!!! requestCode: $requestCode")
        checkIfTheUserHasToken()
    }

    override fun onPermissionDenied(requestCode: Int) {
        L.i(TAG, "onPermissionDenied!!! requestCode: $requestCode")
        checkIfTheUserHasToken()
    }

    private fun checkIfTheUserHasToken() {
        if (UserTokenManager.hasToken()) {
            goToNext(UiMainActivity::class.java)
        }
        else {
            goToNext(UiLoginActivity::class.java)
        }
    }

    private fun goToNext(targetClass: Class<out Activity>) {
        Handler(Looper.getMainLooper()).postDelayed({
            this.startActivitySafely(Intent(this, targetClass))
            this.finish()
        }, 1000)
    }
}