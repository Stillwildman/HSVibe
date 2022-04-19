package com.hsvibe.repositories

import com.hsvibe.callbacks.OnLoadingCallback
import com.hsvibe.model.ApiConst
import com.hsvibe.model.UserTokenManager
import com.hsvibe.model.items.*
import com.hsvibe.model.posts.PostUseCoupon
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

    override suspend fun redeemCoupon(uuid: String): ItemMessage? {
        return UserTokenManager.getAuthorization()?.let {
            DataCallbacks.redeemCoupon(it, uuid, callback)
        }
    }

    override suspend fun getMyCouponListPair(): Pair<List<ItemMyCoupon.ContentData>, List<ItemMyCoupon.ContentData>> {
        return UserTokenManager.getAuthorization()?.let {
            DataCallbacks.getMyCouponList(it, callback)?.let { couponItem ->
                withContext(Dispatchers.Default) {
                    val myCouponList = mutableListOf<ItemMyCoupon.ContentData>()
                    val usedCouponList = mutableListOf<ItemMyCoupon.ContentData>()
                    couponItem.contentData.forEach { contentData ->
                        when (contentData.status) {
                            ApiConst.COUPON_STATUS_NOT_USED -> myCouponList.add(contentData)
                            ApiConst.COUPON_STATUS_USED -> usedCouponList.add(contentData)
                        }
                    }
                    Pair(myCouponList, usedCouponList)
                }
            }
        } ?: Pair(listOf(), listOf())
    }

    override suspend fun getCouponCode(uuid: String): ItemCouponCode? {
        return UserTokenManager.getAuthorization()?.let {
            DataCallbacks.useCoupon(it, PostUseCoupon(uuid), callback)
        }
    }
}