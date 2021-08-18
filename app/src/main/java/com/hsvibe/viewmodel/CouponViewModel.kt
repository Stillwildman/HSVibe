package com.hsvibe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.model.ApiConst
import com.hsvibe.model.items.ItemCoupon
import com.hsvibe.model.items.ItemCouponCategories
import com.hsvibe.paging.BasePagingConfig
import com.hsvibe.paging.CouponDataSource
import com.hsvibe.repositories.CouponRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Vincent on 2021/8/5.
 */
class CouponViewModel(private val couponRepo: CouponRepo) : LoadingStatusViewModel(), BasePagingConfig {

    override fun getPerPageSize(): Int = ApiConst.DEFAULT_LIMIT

    private var contentFlow: Flow<PagingData<ItemCoupon.ContentData>>? = null

    val liveCouponDistrictPairList by lazy { MutableLiveData<List<Pair<String, String>>>() }
    val liveCouponCategories by lazy { MutableLiveData<ItemCouponCategories>() }

    init {
        couponRepo.setLoadingCallback(this)
    }

    fun getCouponFlow(category: Int): Flow<PagingData<ItemCoupon.ContentData>> {
        return contentFlow ?: run {
            Pager(pageConfig) {
                CouponDataSource(category, this)
            }.flow.cachedIn(viewModelScope)
        }.also { contentFlow = it }
    }

    fun getCouponDistricts() {
        viewModelScope.launch(getExceptionHandler()) {
            couponRepo.getCouponDistricts()?.let {
                liveCouponDistrictPairList.value = createCouponDistrictParList(it)
            }
        }
    }

    private suspend fun createCouponDistrictParList(item: ItemCouponCategories): List<Pair<String, String>> {
        return withContext(Dispatchers.Default) {
            val pairList = mutableListOf<Pair<String, String>>()

            pairList.add(Pair(AppController.getAppContext().getString(R.string.all_districts), "0"))

            item.contentData.forEach {
                pairList.add(Pair(it.name, it.id.toString()))
            }
            pairList
        }
    }

    fun getCouponCategories() {
        viewModelScope.launch(getExceptionHandler()) {
            couponRepo.getCouponCategories()?.let {
                liveCouponCategories.value = it
            }
        }
    }
}