package com.hsvibe.model.items

import androidx.annotation.DrawableRes
import com.google.gson.annotations.SerializedName
import com.hsvibe.R

/**
 * Created by Vincent on 2022/5/10.
 */
data class ItemCardList(
    @SerializedName("data")
    val cardData: CardData
) {
    data class CardData(
        val message: String,
        val code: String,
        val user_uuid: String,
        @SerializedName("detail")
        val cardDetailList: MutableList<CardDetail>
    ) {
        data class CardDetail(
            val key: String,
            val name: String,
            val display: String,
            val expire_date: String,
            val bank_number: String,
            val brand: String,
            val issuer: String,
            var isDefault: Boolean = false
        ) {
            @DrawableRes
            fun getBrandIconRes(): Int {
                return when (brand) {
                    "V" -> R.drawable.ic_credit_card_visa
                    "M" -> R.drawable.ic_credit_card_mastercard
                    "J" -> R.drawable.ic_credit_card_jcb
                    else -> 0
                }
            }

            fun getDisplayNumber(): String {
                val sb = StringBuilder()

                display.forEachIndexed { index, char ->
                    if (index > 0 && isDivisibleBy4(index)) {
                        sb.append("-")
                    }
                    sb.append(char)
                }
                return sb.toString()
            }

            private fun isDivisibleBy4(index: Int): Boolean {
                return index % 4 == 0
            }
        }
    }
}