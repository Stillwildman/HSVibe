package com.hsvibe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsvibe.callbacks.OnLoadingCallback
import com.hsvibe.model.ApiConst
import com.hsvibe.model.items.ItemContent
import com.hsvibe.model.items.ItemCoupon
import com.hsvibe.repositories.ContentRepo
import kotlinx.coroutines.launch

/**
 * Created by Vincent on 2021/7/17.
 */
class HomeViewModel(private val contentRepo: ContentRepo) : ViewModel(), OnLoadingCallback {

    val liveLoadingStatus = MutableLiveData<Boolean>()
    val liveErrorMessage = MutableLiveData<String>()

    val liveNews = MutableLiveData<ItemContent>()
    val liveCoupons = MutableLiveData<ItemCoupon>()

    val headerList by lazy { contentRepo.getHeaderItemList() }

    init {
        contentRepo.setLoadingCallback(this)
    }

    override fun onLoadingStart() {
        liveLoadingStatus.postValue(true)
    }

    override fun onLoadingEnd() {
        liveLoadingStatus.postValue(false)
    }

    override fun onLoadingFailed(errorMessage: String?) {
        liveErrorMessage.postValue(errorMessage ?: "")
    }

    fun getHomePageNews() {
        viewModelScope.launch {
            val newsItem = contentRepo.getNews(ApiConst.ORDER_BY_UPDATED, ApiConst.SORTED_BY_DESC, 5, 1)
            newsItem?.let { liveNews.value = it }
        }
    }

    fun getHomePageCoupons() {
        viewModelScope.launch {
            val couponItem = contentRepo.getCoupon(ApiConst.ORDER_BY_UPDATED, ApiConst.SORTED_BY_DESC, 5, 1)
            couponItem?.let { liveCoupons.value = it }
        }
    }

    fun getContentDataSize(): Int {
        return liveNews.value?.contentData?.size ?: 0
    }

    fun getCouponDataSize(): Int {
        return liveCoupons.value?.contentData?.size ?: 0
    }

    fun getContentData(index: Int): ItemContent.ContentData? {
        return liveNews.value?.contentData?.get(index)
    }

    fun getCouponContentData(index: Int): ItemCoupon.ContentData? {
        return liveCoupons.value?.contentData?.get(index)
    }

    fun onMoreClick(apiType: Int) {
        when (apiType) {
            ApiConst.API_TYPE_NEWS -> {
                // TODO
            }
            ApiConst.API_TYPE_COUPON -> {
                // TODO
            }
            ApiConst.API_TYPE_DISCOUNT -> {
                // TODO
            }
            ApiConst.API_TYPE_FOODS -> {
                // TODO
            }
            ApiConst.API_TYPE_HOTEL -> {
                // TODO
            }
        }
    }
}