package com.hsvibe.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hsvibe.model.ApiConst
import com.hsvibe.model.items.ItemAccountBonus
import com.hsvibe.paging.BasePagingConfig
import com.hsvibe.paging.BonusDataSource
import kotlinx.coroutines.flow.Flow

/**
 * Created by Vincent on 2021/8/5.
 */
class BonusViewModel : LoadingStatusViewModel(), BasePagingConfig {

    override fun getPerPageSize(): Int = ApiConst.DEFAULT_LIMIT

    private var bonusFlow: Flow<PagingData<ItemAccountBonus.ContentData>>? = null

    fun getBonusFlow(): Flow<PagingData<ItemAccountBonus.ContentData>> {
        return bonusFlow ?: run {
            Pager(pageConfig) {
                BonusDataSource(this)
            }.flow.cachedIn(viewModelScope)
        }.also { bonusFlow = it }
    }
}