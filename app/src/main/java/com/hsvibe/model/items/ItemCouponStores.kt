package com.hsvibe.model.items

import com.google.gson.annotations.SerializedName

/**
 * Created by Vincent on 2021/8/18.
 */
data class ItemCouponStores(
    @SerializedName("data")
    val contentData: List<ContentData>
) {
    data class ContentData(
        val id: Int,
        val name: String,
        val fullname: String,
        val brand: Brand,
        var columnPosition: Int,
        var rowPosition: Int
    ) {
        data class Brand(
            @SerializedName("data")
            val brandData: List<BrandData>
        ) {
            data class BrandData(
                val id: Int,
                val name: String,
                val is_enable: Int
            )
        }
    }

    fun isStoreEnabled(): Boolean {
        return contentData.takeIf { it.isNotEmpty() }?.first()?.brand?.brandData.takeIf { !it.isNullOrEmpty() }?.first()?.let {
            it.is_enable == 1
        } ?: false
    }

    fun getBrandId(): Int? {
        return contentData.first().brand.brandData.takeIf { !it.isNullOrEmpty() }?.first()?.id
    }
}