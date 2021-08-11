package com.hsvibe.utilities

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Process
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.annotation.StringRes
import com.hsvibe.AppController

/**
 * Created by Vincent on 2021/6/28.
 */
object Utility {

    fun forceCloseTask() {
        Process.killProcess(Process.myPid())
    }

    fun toastShort(msg: String) {
        Toast.makeText(AppController.getAppContext(), msg, Toast.LENGTH_SHORT).show()
    }

    fun toastShort(@StringRes msgResId: Int) {
        toastShort(AppController.getAppContext().getString(msgResId))
    }

    fun toastLong(msg: String) {
        Toast.makeText(AppController.getAppContext(), msg, Toast.LENGTH_LONG).show()
    }

    fun toastLong(@StringRes msgResId: Int) {
        toastLong(AppController.getAppContext().getString(msgResId))
    }

    fun getScreenWidth(): Int {
        val dm: DisplayMetrics = AppController.getAppContext().resources.displayMetrics
        return dm.widthPixels
    }

    fun getScreenHeight(): Int {
        val dm: DisplayMetrics = AppController.getAppContext().applicationContext.resources.displayMetrics
        return dm.heightPixels
    }

    fun convertSecondToMillisecond(second: Long): Long {
        return second * 1000
    }

    fun isNetworkEnabled(): Boolean {
        val connectivityManager = AppController.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= 23) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        L.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        L.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        L.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                        return true
                    }
                }
            }
        }
        else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }
}