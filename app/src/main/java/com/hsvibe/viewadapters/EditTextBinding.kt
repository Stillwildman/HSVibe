package com.hsvibe.viewadapters

import android.text.InputType
import android.widget.EditText
import androidx.databinding.BindingAdapter

/**
 * Created by Vincent on 2022/5/5.
 */

@BindingAdapter("isEditable")
fun isEditable(editText: EditText, isEditable: Boolean) {
    editText.isFocusable = isEditable
    editText.isFocusableInTouchMode = isEditable
    editText.isClickable = isEditable

    if (isEditable) {
        editText.inputType = InputType.TYPE_CLASS_TEXT
    }
    else {
        editText.inputType = InputType.TYPE_NULL
    }
}