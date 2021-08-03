package com.hsvibe.utilities

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.hsvibe.AppController

/**
 * Created by Vincent on 2021/7/26.
 */
object BitmapUtil {

    fun getBitmapFromVectorRes(@DrawableRes vectorRes: Int, size: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(AppController.getAppContext(), vectorRes)

        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        drawable?.let {
            val canvas = Canvas(bitmap)
            it.setBounds(0, 0, size, size)
            it.draw(canvas)
        }

        return bitmap
    }

}