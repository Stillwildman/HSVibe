package com.hsvibe.model.items

import com.google.gson.annotations.SerializedName
import com.hsvibe.AppController
import com.hsvibe.R

/**
 * Created by Vincent on 2022/4/20.
 */
data class ItemBrand(
    @SerializedName("data")
    val contentData: MutableList<ContentData>
) {
    data class ContentData(
        val id: Int = 0,
        val name: String = AppController.getString(R.string.all),
        val is_enable: Boolean = true,
        val coupon_count: Int = 0,
        val store_ids: String? = null,
        val partners: Partners = Partners(listOf()),
        var rowPosition: Int = 0,
        var columnPosition: Int = 0
    ) {
        data class Partners(
            @SerializedName("data")
            val partnersData: List<PartnerData>
        ) {
            data class PartnerData(
                val id: Int,
                val name: String
            )
        }
    }
}