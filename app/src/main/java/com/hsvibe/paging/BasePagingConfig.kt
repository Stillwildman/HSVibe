package com.hsvibe.paging

import androidx.paging.PagingConfig

/**
 * Created by Vincent on 2021/8/4.
 */
interface BasePagingConfig {

    fun getPerPageSize(): Int

    fun getPrefetchDistance(): Int = getPerPageSize()

    fun enablePlaceHolders(): Boolean = false

    val pageConfig: PagingConfig
        get() = PagingConfig(
            pageSize = getPerPageSize(),
            prefetchDistance = getPrefetchDistance(),
            enablePlaceholders = enablePlaceHolders(),
            initialLoadSize = getPerPageSize()
        )
}