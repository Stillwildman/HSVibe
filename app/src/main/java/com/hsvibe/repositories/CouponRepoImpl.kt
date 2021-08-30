package com.hsvibe.repositories

import com.hsvibe.callbacks.OnLoadingCallback
import com.hsvibe.model.items.ItemCouponDistricts
import com.hsvibe.model.items.ItemCouponStores
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

    override suspend fun getCouponDistricts(): ItemCouponDistricts? {
        return withContext(Dispatchers.IO) {
            DataCallbacks.getCouponDistricts(callback)
        }
    }

    override suspend fun getCouponStores(categoryId: Int): ItemCouponStores? {
        return withContext(Dispatchers.IO) {
            DataCallbacks.getCouponStores(categoryId, callback)
        }
    }
}