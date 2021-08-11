package com.hsvibe.callbacks

import com.hsvibe.model.items.ItemCoupon

/**
 * Created by Vincent on 2021/8/12.
 */
interface OnCouponRedeemClickCallback {

    fun onRedeemClick(couponItem: ItemCoupon.ContentData)

}