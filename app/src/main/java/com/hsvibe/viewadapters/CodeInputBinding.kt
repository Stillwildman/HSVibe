package com.hsvibe.viewadapters

import androidx.databinding.BindingAdapter
import com.hsvibe.widgets.CodeInputLayout

/**
 * Created by Vincent on 2022/5/10.
 */

@BindingAdapter("inputLayoutState")
fun setInputLayoutState(codeInputLayout: CodeInputLayout, state: Int) {
    codeInputLayout.setState(state)
}