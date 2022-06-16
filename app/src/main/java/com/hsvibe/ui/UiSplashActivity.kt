package com.hsvibe.ui

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.hsvibe.R
import com.hsvibe.model.ApiConst
import com.hsvibe.model.UserTokenManager
import com.hsvibe.ui.bases.BaseFullScreenActivity
import com.hsvibe.utilities.L
import com.hsvibe.utilities.startActivitySafely
import kotlinx.coroutines.delay

class UiSplashActivity : BaseFullScreenActivity() {

    override fun getLayoutId(): Int = R.layout.activity_splash

    override fun init() {

    }

    override fun onResume() {
        super.onResume()
        checkGooglePlayService()
    }

    private fun checkGooglePlayService() {
        checkPlayServices { isSuccess ->
            if (isSuccess) {
                requireLocationPermission()
            }
            else {
                // Continue without Location permission.
                checkIfTheUserHasToken()
            }
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
        val target = if (intent.extras != null && intent.extras?.containsKey(ApiConst.TYPE) == true && intent.extras?.containsKey(ApiConst.ID) == true) {
            intent.setClass(this, targetClass)
        } else {
            Intent(this, targetClass)
        }

        lifecycleScope.launchWhenResumed {
            delay(1000)
            this@UiSplashActivity.startActivitySafely(target)
            this@UiSplashActivity.finish()
        }
    }
}