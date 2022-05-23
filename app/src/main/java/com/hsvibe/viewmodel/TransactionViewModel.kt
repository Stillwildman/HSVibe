package com.hsvibe.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hsvibe.model.ApiConst
import com.hsvibe.model.items.ItemTransactions
import com.hsvibe.paging.BasePagingConfig
import com.hsvibe.paging.TransactionDataSource
import kotlinx.coroutines.flow.Flow

/**
 * Created by Vincent on 2022/5/23.
 */
class TransactionViewModel : LoadingStatusViewModel(), BasePagingConfig {

    override fun getPerPageSize(): Int = ApiConst.DEFAULT_LIMIT

    private var contentFlow: Flow<PagingData<ItemTransactions.ContentData>>? = null

    fun getContentFlow(): Flow<PagingData<ItemTransactions.ContentData>> {
        return contentFlow ?: run {
            Pager(pageConfig) {
                TransactionDataSource(this)
            }.flow.cachedIn(viewModelScope)
        }.also { contentFlow = it }
    }
}