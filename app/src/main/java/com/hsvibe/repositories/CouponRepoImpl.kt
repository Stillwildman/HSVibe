package com.hsvibe.repositories

import com.hsvibe.callbacks.OnLoadingCallback
import com.hsvibe.model.UserTokenManager
import com.hsvibe.model.items.ItemCoupon
import com.hsvibe.model.items.ItemCouponDistricts
import com.hsvibe.model.items.ItemCouponStores
import com.hsvibe.network.DataCallbacks

/**
 * Created by Vincent on 2021/8/18.
 */
class CouponRepoImpl : CouponRepo {

    private var callback: OnLoadingCallback? = null

    override fun setLoadingCallback(loadingCallback: OnLoadingCallback?) {
        this.callback = loadingCallback
    }

    override suspend fun getCouponDistricts(): ItemCouponDistricts? {
        return DataCallbacks.getCouponDistricts(callback)
    }

    override suspend fun getCouponStores(categoryId: Int): ItemCouponStores? {
        return DataCallbacks.getCouponStores(categoryId, callback)
    }

    override suspend fun groupingStoresByBrand(stores: ItemCouponStores): ItemCouponStores {
        TODO("Not yet implemented")
    }

    override suspend fun getCouponDetail(uuid: String): ItemCoupon? {
        return DataCallbacks.getCouponDetail(uuid, callback)
    }

    override suspend fun redeemCoupon(uuid: String): ItemCoupon? {
        return UserTokenManager.getAuthorization()?.let {
            DataCallbacks.redeemCoupon(it, uuid, callback)
        }
    }
}