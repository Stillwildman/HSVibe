package com.hsvibe.model.items

import android.view.View
import com.hsvibe.AppController
import com.hsvibe.R
import java.text.NumberFormat
import java.util.*

/**
 * Created by Vincent on 2022/5/17.
 */
data class ItemPaymentDisplay(
    var isCreditCardEnabled: Boolean,
    var isPointEnabled: Boolean,
    var selectedCardKey: String?,
    var cardName: String?,
    var cardNumber: String?,
    var cardBrandRes: Int?,
    var selectedPoints: Int = 0,
    var selectedCouponUuid: String? = null,
    var selectedCouponName: String? = null,
    var paymentCode: String? = null
) {
    fun getCardInfoVisibility(): Int {
        return if (isCreditCardEnabled && selectedCardKey != null) View.VISIBLE else View.GONE
    }

    fun getCardInfoText(): String {
        return if (!isCreditCardEnabled || selectedCardKey == null) AppController.getString(R.string.pay_by_cash) else "$cardName $cardNumber"
    }

    fun getPointVisibility(): Int {
        return if (isPointEnabled) View.VISIBLE else View.GONE
    }

    fun getSelectedPointsText(): String {
        return NumberFormat.getInstance(Locale.getDefault()).format(selectedPoints)
    }

    fun getCouponVisibility(): Int {
        return if (selectedCouponUuid != null && selectedCouponName != null) View.VISIBLE else View.GONE
    }

    fun getPaymentCodeVisibility(): Int {
        return if (paymentCode != null) View.VISIBLE else View.GONE
    }
}
