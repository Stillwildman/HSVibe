package com.hsvibe.model.items

import com.google.gson.annotations.SerializedName
import kotlin.math.floor

/**
 * Created by Vincent on 2021/8/14.
 */
data class ItemUserBonus(
    @SerializedName("data")
    val contentData: ContentData
) {
    data class ContentData(
        val balance: Double,
        val expiration_point: Double,
        val expiring_point: Double,
        val accumulate: Double,
        val created_at: String,
        val updated_at: String
    ) {
        fun getBalanceInt(): Int {
            return floor(balance).toInt()
        }
    }
}

