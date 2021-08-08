package com.hsvibe.repositories

import com.hsvibe.callbacks.OnLoadingCallback
import com.hsvibe.model.ApiConst
import com.hsvibe.model.items.ItemBanner
import com.hsvibe.model.items.ItemContent
import com.hsvibe.model.items.ItemCoupon
import com.hsvibe.model.items.ItemHomeHeader

/**
 * Created by Vincent on 2021/7/19.
 */
interface HomeContentRepo {

    fun setLoadingCallback(loadingCallback: OnLoadingCallback?)

    fun getHeaderItemList(): List<ItemHomeHeader>

    suspend fun getNews(orderBy: String = ApiConst.ORDER_BY_UPDATED,
                        sortedBy: String = ApiConst.SORTED_BY_DESC,
                        limit: Int = ApiConst.DEFAULT_LIMIT,
                        page: Int = 1): ItemContent?

    suspend fun getCoupon(orderBy: String = ApiConst.ORDER_BY_UPDATED,
                        sortedBy: String = ApiConst.SORTED_BY_DESC,
                        limit: Int = ApiConst.DEFAULT_LIMIT,
                        page: Int = 1): ItemCoupon?

    suspend fun getBanner(): ItemBanner?

}