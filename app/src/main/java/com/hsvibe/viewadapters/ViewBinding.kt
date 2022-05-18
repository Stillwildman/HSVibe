package com.hsvibe.viewadapters

import android.view.View
import androidx.databinding.BindingAdapter

/**
 * Created by Vincent on 2022/5/18.
 */

@BindingAdapter("selected")
fun selected(view: View, isSelected: Boolean) {
    view.isSelected = isSelected
}