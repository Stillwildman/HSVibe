package com.hsvibe.viewadapters

import android.view.View
import androidx.databinding.BindingAdapter
import com.hsvibe.utilities.Extensions.setOnSingleClickListener

/**
 * Created by Vincent on 2021/8/12.
 */
object SingleClickBinding {

    @JvmStatic
    @BindingAdapter("singleClick")
    fun onSingleClick(view: View, onClick: () -> Unit) {
        view.setOnSingleClickListener { onClick() }
    }

}