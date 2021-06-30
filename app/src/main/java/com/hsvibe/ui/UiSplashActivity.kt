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
        checkUserToken()
    }

    private fun checkUserToken() {
        UserTokenManager.run {
            when (getUserTokenStatus()) {
                STATUS_NULL -> {
                    goToNext(UiLoginActivity::class.java)
                }
                STATUS_EXPIRED -> {
                    // TODO Call refresh token
                }
                STATUS_OK -> {
                    // TODO Go to main page
                }
            }
        }
    }

    private fun goToNext(targetClass: Class<out Activity>) {
        this.startActivity(Intent(this, targetClass))
        this.finish()
    }
}