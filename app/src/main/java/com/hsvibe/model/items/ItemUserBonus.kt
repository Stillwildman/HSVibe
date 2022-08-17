package com.hsvibe.model.items

import com.google.gson.annotations.SerializedName
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
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
        fun getBalanceText(): String {
            return if (balance < 1) {
                val decimalFormat = DecimalFormat("#.##").apply { roundingMode = RoundingMode.FLOOR }
                decimalFormat.format(balance)
            }
            else {
                NumberFormat.getInstance().format(floor(balance).toInt())
            }
        }
    }
}

