package com.hsvibe.viewadapters

import android.view.View
import androidx.databinding.BindingAdapter
import com.hsvibe.callbacks.SingleClickListener

/**
 * Created by Vincent on 2021/8/12.
 */
object SingleClickBinding {

    @JvmStatic
    @BindingAdapter("singleClick")
    fun onSingleClick(view: View, onClick: () -> Unit) {
        view.setOnClickListener(object : SingleClickListener() {
            override fun onSingleClick(v: View) {
                onClick()
            }
        })

    }

}