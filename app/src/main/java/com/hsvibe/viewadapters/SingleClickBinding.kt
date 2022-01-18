package com.hsvibe.viewadapters

import android.view.View
import androidx.databinding.BindingAdapter
import com.hsvibe.utilities.setOnSingleClickListener

/**
 * Created by Vincent on 2021/8/12.
 */
@BindingAdapter("singleClick")
fun onSingleClick(view: View, clickListener: View.OnClickListener?) {
    view.setOnSingleClickListener { clickListener?.onClick(it) }
}