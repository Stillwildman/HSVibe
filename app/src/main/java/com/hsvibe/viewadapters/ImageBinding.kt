package com.hsvibe.viewadapters

import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.hsvibe.AppController

/**
 * Created by Vincent on 2021/8/23.
 */
object ImageBinding {

    @JvmStatic
    @BindingAdapter("setImageByRes")
    fun setImageByResId(imageView: AppCompatImageView, @DrawableRes imageRes: Int) {
        imageView.setImageDrawable(ContextCompat.getDrawable(AppController.getAppContext(), imageRes))
    }

}