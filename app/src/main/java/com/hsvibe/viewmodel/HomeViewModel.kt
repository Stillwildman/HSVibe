package com.hsvibe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hsvibe.model.ApiConst
import com.hsvibe.model.Navigation
import com.hsvibe.model.items.ItemBanner
import com.hsvibe.model.items.ItemContent
import com.hsvibe.model.items.ItemCoupon
import com.hsvibe.repositories.HomeContentRepo
import kotlinx.coroutines.launch

/**
 * Created by Vincent on 2021/7/17.
 */
class HomeViewModel(private val homeContentRepo: HomeContentRepo, private val mainViewModel: MainViewModel) : LoadingStatusViewModel() {

    val headerList by lazy { homeContentRepo.getHeaderItemList() }

    val liveNews = MutableLiveData<ItemContent>()
    val liveCoupons = MutableLiveData<ItemCoupon>()
    val liveBanner = MutableLiveData<ItemBanner>()

    init {
        homeContentRepo.setLoadingCallback(this)
    }

    fun getHomePageNews() {
        viewModelScope.launch {
            val news = homeContentRepo.getNews(ApiConst.ORDER_BY_UPDATED, ApiConst.SORTED_BY_DESC, 5, 1)
            news?.let { liveNews.value = it }
        }
    }

    fun getHomePageCoupons() {
        viewModelScope.launch {
            val coupons = homeContentRepo.getCoupon(ApiConst.ORDER_BY_UPDATED, ApiConst.SORTED_BY_DESC, 5, 1)
            coupons?.let { liveCoupons.value = it }
        }
    }

    fun getHomePageBanner() {
        viewModelScope.launch {
            val banners = homeContentRepo.getBanner()
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

    fun onNewsClick(index: Int) {
        mainViewModel.onNavigating(Navigation.ClickingNews(index))
    }

    fun onCouponClick(couponItem: ItemCoupon.ContentData?) {
        mainViewModel.onNavigating(Navigation.ClickingCoupon(couponItem))
    }

    fun onBannerClick(bannerItem: ItemBanner.ContentData?) {
        mainViewModel.onNavigating(Navigation.ClickingBanner(bannerItem))
    }
}