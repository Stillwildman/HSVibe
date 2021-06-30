package com.hsvibe.utilities

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
}