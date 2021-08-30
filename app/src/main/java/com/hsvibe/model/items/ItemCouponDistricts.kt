package com.hsvibe.model.items

import com.google.gson.annotations.SerializedName

/**
 * Created by Vincent on 2021/8/30.
 */
data class ItemCouponDistricts(
    @SerializedName("data")
    val contentData: List<ContentData>
) {
    data class ContentData(
        val children: Children
    ) {
        data class Children(
            @SerializedName("data")
            val childrenData: List<ChildrenData>
        ) {
            data class ChildrenData(
                val id: Int,
                val name: String,
                val parent_id: Int
            )
        }
    }
}