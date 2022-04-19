package com.hsvibe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.callbacks.DataSourceParamInterface
import com.hsvibe.model.ApiConst
import com.hsvibe.model.items.ItemCoupon
import com.hsvibe.model.items.ItemCouponDistricts
import com.hsvibe.model.items.ItemCouponStores
import com.hsvibe.model.items.ItemMyCoupon
import com.hsvibe.paging.BasePagingConfig
import com.hsvibe.paging.CouponDataSource
import com.hsvibe.repositories.CouponRepo
import com.hsvibe.tasks.ApiStatusException
import com.hsvibe.utilities.L
import com.hsvibe.utilities.Utility
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Vincent on 2021/8/5.
 */
class CouponViewModel(private val couponRepo: CouponRepo) : LoadingStatusViewModel(), DataSourceParamInterface<Int>, BasePagingConfig {

    override fun getPerPageSize(): Int = ApiConst.DEFAULT_LIMIT

    private var couponDataSource: CouponDataSource? = null

    private var contentFlow: Flow<PagingData<ItemCoupon.ContentData>>? = null

    val liveCouponDistrictPairList by lazy { MutableLiveData<List<Pair<String, String>>>() }
    val liveCouponStores by lazy { MutableLiveData<ItemCouponStores>() }
    val liveCouponDetail by lazy { MutableLiveData<ItemCoupon.ContentData>() }

    val liveMyCouponList by lazy { MutableLiveData<List<ItemMyCoupon.ContentData>>() }
    val liveUsedCouponList by lazy { MutableLiveData<List<ItemMyCoupon.ContentData>>() }

    val liveCouponCode by lazy { MutableLiveData<String>() }

    private var storeId = ApiConst.ALL

    init {
        couponRepo.setLoadingCallback(this)
    }

    fun getCouponFlow(storeId: Int): Flow<PagingData<ItemCoupon.ContentData>> {
        this.storeId = storeId

        return contentFlow ?: run {
            Pager(pageConfig) {
                CouponDataSource(this, this).also { couponDataSource = it }
            }.flow.cachedIn(viewModelScope)
        }.also { contentFlow = it }
    }

    fun refreshCouponFlowByStoreId(storeId: Int) {
        this.storeId = storeId
        couponDataSource?.invalidate()
    }

    override fun getParams(): Int {
        return storeId
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

            pairList.add(Pair(AppController.getString(R.string.all_districts), "0"))

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

    fun redeemCoupon(uuid: String, onRedeemFinished: (isSuccess: Boolean) -> Unit) {
        viewModelScope.launch(getRedeemExceptionHandler {
            onRedeemFinished(false)
        }) {
            couponRepo.redeemCoupon(uuid)?.let {
                onRedeemFinished(true)
            }
        }
    }

    fun updateCouponDetail(uuid: String) {
        viewModelScope.launch(getExceptionHandler()) {
            couponRepo.getCouponDetail(uuid)?.takeIf { it.contentData.isNotEmpty() }?.let {
                liveCouponDetail.value = it.contentData.first()
            }
        }
    }

    fun loadMyCouponListPair() {
        if (liveMyCouponList.value != null && liveUsedCouponList.value != null) {
            return
        }
        viewModelScope.launch(getExceptionHandler()) {
            couponRepo.getMyCouponListPair().let {
                liveMyCouponList.value = it.first
                liveUsedCouponList.value = it.second
            }
        }
    }

    fun refreshMyCouponListPair() {
        liveMyCouponList.value = null
        liveUsedCouponList.value = null
        loadMyCouponListPair()
    }

    fun useCoupon(uuid: String) {
        viewModelScope.launch(getExceptionHandler()) {
            couponRepo.getCouponCode(uuid)?.let {
                liveCouponCode.value = it.getCode().toString()
            }
        }
    }

    private fun getRedeemExceptionHandler(onRedeemFailed: () -> Unit ): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, throwable ->
            L.e("Handle Coroutine Exception!!!")
            when {
                throwable is ApiStatusException -> {
                    onRedeemFailed()
                    L.e("Api Error!!\nCode: ${throwable.statusCode}\nMsg: ${throwable.errorMessage}")
                }
                !Utility.isNetworkEnabled() -> {
                    L.e("Network is not working!!!")
                    Utility.toastLong("Network is not working!!!")
                }
                else -> {
                    Utility.toastLong(R.string.unknown_network_error)
                }
            }
            onLoadingEnd()
            throwable.printStackTrace()
        }
    }
}