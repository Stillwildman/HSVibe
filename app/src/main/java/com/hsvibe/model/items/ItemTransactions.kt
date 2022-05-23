package com.hsvibe.model.items

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.google.gson.annotations.SerializedName
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.model.ApiConst
import java.text.NumberFormat

/**
 * Created by Vincent on 2022/5/23.
 */
data class ItemTransactions(
    @SerializedName("data")
    val contentData: List<ContentData>,
    val meta: Meta
) {
    data class ContentData(
        val id: Int,
        val transaction_code: String,
        val store_name: String,
        val description: String,
        val action: String,
        val payment_amount: Int,
        val is_success: Boolean,
        val created_at: String
    ) {
        fun getStatusText(): String {
            val textRes = if (action == ApiConst.PAY) R.string.pay_amount_is else R.string.refund_amount_is
            val amountText = NumberFormat.getInstance().format(payment_amount)
            return AppController.getAppContext().getString(textRes, description, amountText)
        }

        @ColorInt
        fun getStatusColor(): Int {
            return if (action == ApiConst.PAY) Color.WHITE else ContextCompat.getColor(AppController.getAppContext(), R.color.md_red_A200)
        }
    }

    data class Meta(
        val pagination: Pagination
    ) {
        data class Pagination(
            val total: Int,
            val per_page: Int,
            val current_page: Int,
            val total_pages: Int
        )
    }
}