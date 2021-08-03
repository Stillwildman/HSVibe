package com.hsvibe.utilities

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.hsvibe.AppController

/**
 * Created by Vincent on 2021/7/26.
 */
object DrawableUtil {

    fun getDrawableFromVectorRes(@DrawableRes vectorRes: Int, size: Int): Drawable? {
        return ContextCompat.getDrawable(AppController.getAppContext(), vectorRes)?.apply {
            setBounds(0, 0, size, size)
        }
    }

}