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
import com.hsvibe.model.items.ItemCouponDistricts
import com.hsvibe.model.items.ItemCouponStores
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

    private var couponDataSource: CouponDataSource? = null

    private var contentFlow: Flow<PagingData<ItemCoupon.ContentData>>? = null

    val liveCouponDistrictPairList by lazy { MutableLiveData<List<Pair<String, String>>>() }
    val liveCouponStores by lazy { MutableLiveData<ItemCouponStores>() }

    init {
        couponRepo.setLoadingCallback(this)
    }

    fun getCouponFlow(storeId: Int): Flow<PagingData<ItemCoupon.ContentData>> {
        return contentFlow ?: run {
            Pager(pageConfig) {
                CouponDataSource(storeId, this).also { couponDataSource = it }
            }.flow.cachedIn(viewModelScope)
        }.also { contentFlow = it }
    }

    fun refreshCouponFlowByStoreId(storeId: Int) {
        couponDataSource?.setStoreId(storeId)
    }

    fun getCouponDistricts() {
        viewModelScope.launch(getExceptionHandler()) {
            couponRepo.getCouponDistricts()?.let {
                liveCouponDistrictPairList.value = createCouponDistrictParList(it)
            }
        }
    }

    private suspend fun createCouponDistrictParList(item: ItemCouponDistricts): List<Pair<String, String>> {
        return withContext(Dispatchers.Default) {
            val pairList = mutableListOf<Pair<String, String>>()

            pairList.add(Pair(AppController.getAppContext().getString(R.string.all_districts), "0"))

            item.contentData.forEach {
                it.children.childrenData.forEach { data ->
                    pairList.add(Pair(data.name, data.id.toString()))
                }
            }
            pairList
        }
    }

    fun getCouponStores(categoryId: Int) {
        viewModelScope.launch(getExceptionHandler()) {
            couponRepo.getCouponStores(categoryId)?.let {
                liveCouponStores.value = it
            }
        }
    }
}