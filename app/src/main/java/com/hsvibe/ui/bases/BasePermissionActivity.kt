package com.hsvibe.ui.bases

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import com.hsvibe.utilities.PermissionCheckHelper

/**
 * Created by Vincent on 2021/6/28.
 */
abstract class BasePermissionActivity : AppCompatActivity() {

    abstract fun onPermissionGranted(requestCode: Int)

    abstract fun onPermissionDenied(requestCode: Int)

    protected fun checkLocationPermission() {
        if (PermissionCheckHelper.checkLocationPermission(this)) {
            onPermissionGranted(PermissionCheckHelper.PERMISSION_REQUEST_CODE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        var allGranted = true

        grantResults.forEach {
            if (it == PackageManager.PERMISSION_DENIED) {
                allGranted = false
            }
        }

        if (allGranted) {
            onPermissionGranted(requestCode)
        }
        else {
            onPermissionDenied(requestCode)
        }
    }
}