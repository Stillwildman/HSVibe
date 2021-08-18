package com.hsvibe.repositories

import com.hsvibe.callbacks.OnLoadingCallback
import com.hsvibe.model.items.ItemCouponCategories
import com.hsvibe.network.DataCallbacks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Created by Vincent on 2021/8/18.
 */
class CouponRepoImpl : CouponRepo {

    private var callback: OnLoadingCallback? = null

    override fun setLoadingCallback(loadingCallback: OnLoadingCallback?) {
        this.callback = loadingCallback
    }

    override suspend fun getCouponDistricts(): ItemCouponCategories? {
        return withContext(Dispatchers.IO) {
            DataCallbacks.getCouponDistricts(callback)
        }
    }

    override suspend fun getCouponCategories(): ItemCouponCategories? {
        return withContext(Dispatchers.IO) {
            DataCallbacks.getCouponCategories(callback)
        }
    }
}