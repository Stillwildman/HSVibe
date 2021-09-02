package com.hsvibe.repositories

import com.hsvibe.callbacks.OnLoadingCallback
import com.hsvibe.model.items.ItemCoupon
import com.hsvibe.model.items.ItemCouponDistricts
import com.hsvibe.model.items.ItemCouponStores

/**
 * Created by Vincent on 2021/8/18.
 */
interface CouponRepo {

    fun setLoadingCallback(loadingCallback: OnLoadingCallback?)

    suspend fun getCouponDistricts(): ItemCouponDistricts?

    suspend fun getCouponStores(categoryId: Int): ItemCouponStores?

    suspend fun getCouponDetail(uuid: String): ItemCoupon?

    suspend fun redeemCoupon(uuid: String): ItemCoupon?

}