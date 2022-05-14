package com.hsvibe.model.items

import com.hsvibe.model.ApiConst

/**
 * Created by Vincent on 2022/4/5.
 */
data class ItemMessage(
    val message: String,
    val code: String?
) {
    fun isSuccess(): Boolean {
        return message == ApiConst.SUCCESS || code == null
    }
}
