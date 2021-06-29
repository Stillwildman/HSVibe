package com.hsvibe.ui

import android.app.Activity
import android.content.Intent
import com.hsvibe.R
import com.hsvibe.model.UserTokenManager
import com.hsvibe.ui.bases.BaseFullScreenActivity

class UiSplashActivity : BaseFullScreenActivity() {

    override fun getLayoutId(): Int = R.layout.activity_splash

    override fun init() {
        checkLocationPermission()
        // TODO Check Google Play service
    }

    override fun onPermissionGranted(requestCode: Int) {
        checkUserToken()
    }

    override fun onPermissionDenied(requestCode: Int) {
        // TODO TBD
    }

    private fun checkUserToken() {
        if (UserTokenManager.hasUserToken()) {
            if (UserTokenManager.isTokenExpired()) {
                // TODO Call refresh token
            }
            // TODO Go to main page
        }
        else {
            // TODO Add ContextUtil and Add startActivitySafely() ext function.
            goToNext(UiLoginActivity::class.java)
        }
    }

    private fun goToNext(targetClass: Class<out Activity>) {
        this.startActivity(Intent(this, targetClass))
        this.finish()
    }
}