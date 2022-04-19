package com.hsvibe.model.items

import com.google.gson.annotations.SerializedName

/**
 * Created by Vincent on 2022/4/19.
 */
data class ItemCouponCode(
    val message: String,
    @SerializedName("data")
    val contentData: Data
) {
    data class Data(
        val code: Long
    )

    fun getCode(): Long {
        return contentData.code
    }
}

