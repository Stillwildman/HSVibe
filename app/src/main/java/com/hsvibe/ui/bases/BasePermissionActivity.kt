package com.hsvibe.ui.bases

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.hsvibe.model.Const
import com.hsvibe.utilities.L
import com.hsvibe.utilities.PermissionCheckHelper
import com.hsvibe.utilities.Utility

/**
 * Created by Vincent on 2021/6/28.
 */
abstract class BasePermissionActivity : AppCompatActivity() {

    abstract fun onPermissionGranted(requestCode: Int)

    abstract fun onPermissionDenied(requestCode: Int)

    protected fun requireLocationPermission() {
        if (PermissionCheckHelper.checkLocationPermission(this)) {
            onPermissionGranted(PermissionCheckHelper.PERMISSION_REQUEST_CODE_LOCATION)
        }
    }

    protected fun hasLocationPermission(): Boolean {
        return PermissionCheckHelper.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    protected fun requireCameraPermission() {
        if (PermissionCheckHelper.checkCameraPermission(this)) {
            onPermissionGranted(PermissionCheckHelper.PERMISSION_REQUEST_CODE_CAMERA)
        }
    }

    protected fun hasCameraPermission(): Boolean {
        return PermissionCheckHelper.hasPermission(this, Manifest.permission.CAMERA)
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

    protected fun checkPlayServices(): Boolean {
        val apiAvailability: GoogleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode: Int = apiAvailability.isGooglePlayServicesAvailable(this)

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, Const.PLAY_SERVICES_RESOLUTION_REQUEST)?.show()
            } else {
                Utility.toastShort("This device is not supported PlayServices.")
                L.i("This device is not supported PlayServices.")
            }
            return false
        }
        return true
    }
}