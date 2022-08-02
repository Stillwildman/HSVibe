package com.hsvibe.model.items

import com.google.gson.annotations.SerializedName

/**
 * Created by Vincent on 2022/6/8.
 */
data class ItemPointTransfer(
    @SerializedName("data")
    val contentData: ContentData
) {
    data class ContentData(
        val income: Income,
        val expenses: Expenses
    ) {
        data class Income(
            val id: Int,
            val operate: String,
            val point: Int,
            val balance: Double,
            val description: String,
            val note: String,
            val created_at: String,
            val updated_at: String
        )

        data class Expenses(
            val id: Int,
            val operate: String,
            val point: Int,
            val balance: Double,
            val description: String,
            val note: String,
            val created_at: String,
            val updated_at: String
        )
    }
}