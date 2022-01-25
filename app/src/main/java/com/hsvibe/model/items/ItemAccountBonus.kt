package com.hsvibe.model.items

import com.google.gson.annotations.SerializedName

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
            return if (isIncome()) point else "-$point"
        }

        private fun isIncome(): Boolean {
            return operate == "income"
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






