package com.hsvibe.repositories

import androidx.annotation.StringRes
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.callbacks.OnLoadingCallback
import com.hsvibe.model.ApiConst
import com.hsvibe.model.items.ItemBanner
import com.hsvibe.model.items.ItemContent
import com.hsvibe.model.items.ItemCoupon
import com.hsvibe.model.items.ItemHomeHeader
import com.hsvibe.network.DataCallbacks

/**
 * Created by Vincent on 2021/7/19.
 */
class HomeContentRepoImpl : HomeContentRepo {

    private var loadingCallback: OnLoadingCallback? = null

    override fun setLoadingCallback(loadingCallback: OnLoadingCallback?) {
        this.loadingCallback = loadingCallback
    }

    override fun getHeaderItemList(): List<ItemHomeHeader> {
        return listOf(
            ItemHomeHeader(getTitleString(R.string.hot_news), true, ApiConst.API_TYPE_NEWS),
            ItemHomeHeader(getTitleString(R.string.hot_coupon), true, ApiConst.API_TYPE_COUPON),
            ItemHomeHeader(getTitleString(R.string.more_discount), false, ApiConst.API_TYPE_DISCOUNT),
            ItemHomeHeader(getTitleString(R.string.hilai_foods), true, ApiConst.API_TYPE_FOODS),
            ItemHomeHeader(getTitleString(R.string.hilai_hotel), true, ApiConst.API_TYPE_HOTEL)
        )
    }

    override suspend fun getNews(orderBy: String, sortedBy: String, limit: Int, page: Int): ItemContent? {
        return DataCallbacks.getContent(ApiConst.CATEGORY_NEWS, orderBy, sortedBy, limit, page, loadingCallback)
    }

    override suspend fun getCoupon(orderBy: String, sortedBy: String, limit: Int, page: Int): ItemCoupon? {
        return DataCallbacks.getCoupon(orderBy, sortedBy, limit, page, loadingCallback)
    }

    override suspend fun getBanner(): ItemBanner? {
        return DataCallbacks.getBanner(loadingCallback)
    }

    private fun getTitleString(@StringRes titleRes: Int): String {
        return AppController.getAppContext().getString(titleRes)
    }
}