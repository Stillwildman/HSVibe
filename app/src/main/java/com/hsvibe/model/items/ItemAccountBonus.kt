package com.hsvibe.model.items

import com.google.gson.annotations.SerializedName
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
        val point: String,
        val balance: String,
        val description: String,
        val note: String,
        val created_at: String,
        val updated_at: String
    ) {
        fun getPointText(): String {
            val pointText = NumberFormat.getInstance().format(point.toIntOrNull() ?: 0)
            return if (isIncome()) pointText else "-$pointText"
        }

        private fun isIncome(): Boolean {
            return operate == "income"
        }

        fun getDesc(): String {
            return description.takeIf { it.isNotEmpty() } ?: note
        }

        fun getBalanceText(): String {
            return NumberFormat.getInstance().format(balance.toDoubleOrNull()?.toInt() ?: 0)
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






