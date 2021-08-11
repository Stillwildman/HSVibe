package com.hsvibe.utilities

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.hsvibe.R

/**
 * Created by Vincent on 2021/6/28.
 */
object PermissionCheckHelper {

    const val PERMISSION_REQUEST_CODE_LOCATION = 100
    const val PERMISSION_REQUEST_CODE_CAMERA = 200

    private val PERMISSIONS_LOCATION = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
    } else {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    fun checkLocationPermission(activity: Activity): Boolean {
        return checkPermission(activity, PERMISSION_REQUEST_CODE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, PERMISSIONS_LOCATION)
    }

    fun hasPermission(activity: Activity, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkPermission(activity: Activity, requestCode: Int, permission: String, requestedPermissions: Array<String>): Boolean {
        return if (hasPermission(activity, permission)) {
            true
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                showRationaleDialog(activity, requestCode) { _, _ ->
                    ActivityCompat.requestPermissions(activity, requestedPermissions, requestCode)
                }
            } else {
                ActivityCompat.requestPermissions(activity, requestedPermissions, requestCode)
            }
            false
        }
    }

    private fun showRationaleDialog(activity: Activity, requestCode: Int, clickListener: DialogInterface.OnClickListener) {
        val titleRes: Int
        val contentRes: Int

        when (requestCode) {
            PERMISSION_REQUEST_CODE_LOCATION -> {
                titleRes = R.string.permission_requires_location_title
                contentRes = R.string.permission_requires_location_content
            }
            PERMISSION_REQUEST_CODE_CAMERA -> {
                titleRes = R.string.permission_requires_camera_title
                contentRes = R.string.permission_requires_camera_content
            }
            else -> {
                titleRes = 0
                contentRes = 0
            }
        }

        AlertDialog.Builder(activity)
            .setTitle(titleRes)
            .setMessage(contentRes)
            .setPositiveButton(R.string.ok, clickListener)
            .setCancelable(false)
            .create()
            .show()
    }
}