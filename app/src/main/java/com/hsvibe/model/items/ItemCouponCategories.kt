package com.hsvibe.model.items

import com.google.gson.annotations.SerializedName

/**
 * Created by Vincent on 2021/8/18.
 */
data class ItemCouponCategories(
    @SerializedName("data")
    val contentData: List<ContentData>
) {
    data class ContentData(
        val id: Int,
        val name: String,
        val type: String,
        var columnPosition: Int,
        var rowPosition: Int
    )
}