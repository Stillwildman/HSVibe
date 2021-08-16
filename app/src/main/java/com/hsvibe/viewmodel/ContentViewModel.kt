package com.hsvibe.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hsvibe.model.ApiConst
import com.hsvibe.model.items.ItemContent
import com.hsvibe.paging.BasePagingConfig
import com.hsvibe.paging.ContentDataSource
import kotlinx.coroutines.flow.Flow

/**
 * Created by Vincent on 2021/8/5.
 */
class ContentViewModel : LoadingStatusViewModel(), BasePagingConfig {

    override fun getPerPageSize(): Int = ApiConst.DEFAULT_LIMIT

    private var contentFlow: Flow<PagingData<ItemContent.ContentData>>? = null

    fun getContentFlow(category: Int): Flow<PagingData<ItemContent.ContentData>> {
        return contentFlow ?: run {
            Pager(pageConfig) {
                ContentDataSource(category, this)
            }.flow.cachedIn(viewModelScope)
        }.also { contentFlow = it }
    }
}