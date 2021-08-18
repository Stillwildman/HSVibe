package com.hsvibe.repositories

import com.hsvibe.callbacks.OnLoadingCallback
import com.hsvibe.model.items.ItemCouponCategories

/**
 * Created by Vincent on 2021/8/18.
 */
interface CouponRepo {

    fun setLoadingCallback(loadingCallback: OnLoadingCallback?)

    suspend fun getCouponDistricts(): ItemCouponCategories?

    suspend fun getCouponCategories(): ItemCouponCategories?

}