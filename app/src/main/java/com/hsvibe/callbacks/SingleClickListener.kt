package com.hsvibe.callbacks

import android.view.View
import com.hsvibe.utilities.L

/**
 * Created by Vincent on 2021/8/12.
 */
abstract class SingleClickListener : View.OnClickListener {

    companion object {
        private const val MIN_CLICK_INTERVAL: Long = 500L
    }

    private var lastClickTime: Long = 0L

    abstract fun onSingleClick(v: View)

    override fun onClick(v: View) {
        val now = System.currentTimeMillis()

        if (lastClickTime == 0L || now - lastClickTime > MIN_CLICK_INTERVAL) {
            L.i("onSingleClick!!!")
            onSingleClick(v)
        }
        else {
            L.i("Click still not available!!!")
        }

        lastClickTime = now
    }
}