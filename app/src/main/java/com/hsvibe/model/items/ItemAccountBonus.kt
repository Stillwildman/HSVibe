package com.hsvibe.model.items

import com.google.gson.annotations.SerializedName
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat

/**
 * Created by Vincent on 2021/8/14.
 */
data class ItemAccountBonus(
    @SerializedName("data")
    val contentData: List<ContentData>,
    val meta: Meta
) {
    data class ContentData(
        val operate: String,
        val point: Double,
        val balance: String,
        val description: String,
        val note: String,
        val created_at: String,
        val updated_at: String
    ) {
        fun getPointText(): String {
            val pointText = if (point < 1) {
                val decimalFormat = DecimalFormat("#.##").apply { roundingMode = RoundingMode.FLOOR }
                decimalFormat.format(point)
            }
            else {
                point.toInt().toString()
            }
            return if (isIncome()) pointText else "-$pointText"
        }

        private fun isIncome(): Boolean {
            return operate == "income"
        }

        fun getDesc(): String {
            return description.takeIf { it.isNotEmpty() } ?: note
        }

        fun getBalanceText(): String {
            val balanceDouble = balance.toDoubleOrNull() ?: 0.0

            return if (balanceDouble < 1) {
                val decimalFormat = DecimalFormat("#.##").apply { roundingMode = RoundingMode.FLOOR }
                decimalFormat.format(balanceDouble)
            }
            else {
                NumberFormat.getInstance().format(balanceDouble.toInt())
            }
        }
    }

    data class Meta(
        val pagination: Pagination
    ) {
        data class Pagination(
            val total: Int,
            val count: Int,
            val per_page: Int,
            val current_page: Int,
            val total_pages: Int
        )
    }
}






