package com.hsvibe.utilities

import android.os.Build

/**
 * Created by Vincent on 2021/7/5.
 */
object DeviceUtil {

    fun getDeviceType(): String {
        return "android"
    }

    fun getDeviceBrand(): String {
        return Build.BRAND
    }

    fun getDeviceModel(): String {
        return Build.MODEL
    }

    fun getOSVersion(): String {
        return Build.VERSION.RELEASE
    }

    fun getCombinedDeviceModel(): String {
        return "${getDeviceBrand()} ${getDeviceModel()} / OS ${getOSVersion()}"
    }
}