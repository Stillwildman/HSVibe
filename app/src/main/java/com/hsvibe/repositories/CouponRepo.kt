package com.hsvibe.repositories

import com.hsvibe.callbacks.OnLoadingCallback
import com.hsvibe.model.items.*

/**
 * Created by Vincent on 2021/8/18.
 */
interface CouponRepo {

    fun setLoadingCallback(loadingCallback: OnLoadingCallback?)

    suspend fun getCouponDistricts(): ItemCouponDistricts?

    suspend fun getCouponBrands(categoryId: Int): ItemCouponBrand?

    suspend fun getCouponDetail(uuid: String): ItemCoupon?

    suspend fun redeemCoupon(uuid: String): ItemMessage?

    suspend fun getMyCouponListPair(): Pair<List<ItemMyCoupon.ContentData>, List<ItemMyCoupon.ContentData>>

    suspend fun getCouponCode(uuid: String): ItemCouponCode?
}