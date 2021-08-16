package com.hsvibe.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hsvibe.model.ApiConst
import com.hsvibe.model.items.ItemCoupon
import com.hsvibe.paging.BasePagingConfig
import com.hsvibe.paging.CouponDataSource
import kotlinx.coroutines.flow.Flow

/**
 * Created by Vincent on 2021/8/5.
 */
class CouponViewModel : LoadingStatusViewModel(), BasePagingConfig {

    override fun getPerPageSize(): Int = ApiConst.DEFAULT_LIMIT

    private var contentFlow: Flow<PagingData<ItemCoupon.ContentData>>? = null

    fun getCouponFlow(category: Int): Flow<PagingData<ItemCoupon.ContentData>> {
        return contentFlow ?: run {
            Pager(pageConfig) {
                CouponDataSource(category, this)
            }.flow.cachedIn(viewModelScope)
        }.also { contentFlow = it }
    }
}