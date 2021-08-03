package com.hsvibe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsvibe.callbacks.OnLoadingCallback
import com.hsvibe.model.ApiConst
import com.hsvibe.model.Navigation
import com.hsvibe.model.items.ItemBanner
import com.hsvibe.model.items.ItemContent
import com.hsvibe.model.items.ItemCoupon
import com.hsvibe.repositories.ContentRepo
import kotlinx.coroutines.launch

/**
 * Created by Vincent on 2021/7/17.
 */
class HomeViewModel(private val contentRepo: ContentRepo, private val mainViewModel: MainViewModel) : ViewModel(), OnLoadingCallback {

    val liveLoadingStatus = MutableLiveData<Boolean>()
    val liveErrorMessage = MutableLiveData<String>()

    val headerList by lazy { contentRepo.getHeaderItemList() }

    val liveNews = MutableLiveData<ItemContent>()
    val liveCoupons = MutableLiveData<ItemCoupon>()
    val liveBanner = MutableLiveData<ItemBanner>()

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
            val news = contentRepo.getNews(ApiConst.ORDER_BY_UPDATED, ApiConst.SORTED_BY_DESC, 5, 1)
            news?.let { liveNews.value = it }
        }
    }

    fun getHomePageCoupons() {
        viewModelScope.launch {
            val coupons = contentRepo.getCoupon(ApiConst.ORDER_BY_UPDATED, ApiConst.SORTED_BY_DESC, 5, 1)
            coupons?.let { liveCoupons.value = it }
        }
    }

    fun getHomePageBanner() {
        viewModelScope.launch {
            val banners = contentRepo.getBanner()
            banners?.let { liveBanner.value = it }
        }
    }

    fun getHomePageHilaiFoods() {

    }

    fun getHomePageHilaiHotel() {

    }

    fun getContentDataSize(): Int {
        return liveNews.value?.contentData?.size ?: 0
    }

    fun getCouponDataSize(): Int {
        return liveCoupons.value?.contentData?.size ?: 0
    }

    fun getBannerDataSize(): Int {
        return liveBanner.value?.contentData?.size ?: 0
    }

    fun getContentData(index: Int): ItemContent.ContentData? {
        return liveNews.value?.contentData?.get(index)
    }

    fun getCouponContentData(index: Int): ItemCoupon.ContentData? {
        return liveCoupons.value?.contentData?.get(index)
    }

    fun getBannerContentData(index: Int): ItemBanner.ContentData? {
        return liveBanner.value?.contentData?.get(index)
    }

    fun onMoreClick(apiType: Int) {
        mainViewModel.onNavigating(Navigation.ClickingMore(apiType))
    }

    fun onNewsClick(newsItem: ItemContent.ContentData?) {
        mainViewModel.onNavigating(Navigation.ClickingNews(newsItem))
    }

    fun onCouponClick(couponItem: ItemCoupon.ContentData?) {
        mainViewModel.onNavigating(Navigation.ClickingCoupon(couponItem))
    }

    fun onBannerClick(bannerItem: ItemBanner.ContentData?) {
        mainViewModel.onNavigating(Navigation.ClickingBanner(bannerItem))
    }
}