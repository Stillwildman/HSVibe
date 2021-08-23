package com.hsvibe.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hsvibe.callbacks.OnLoadingCallback
import com.hsvibe.model.items.ItemCoupon
import com.hsvibe.network.DataCallbacks
import com.hsvibe.utilities.L
import retrofit2.HttpException
import java.io.IOException

/**
 * Created by Vincent on 2021/8/5.
 */
class CouponDataSource(private val category: Int, private val loadingCallback: OnLoadingCallback?) : PagingSource<Int, ItemCoupon.ContentData>() {

    override fun getRefreshKey(state: PagingState<Int, ItemCoupon.ContentData>): Int? {
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ItemCoupon.ContentData> {
        try {
            val pageKey = params.key ?: 1

            L.i("LoadSize: ${params.loadSize}")

            val response = DataCallbacks.getCoupon(     // TODO Add category parameter.
                limit = params.loadSize,
                page = pageKey,
                loadingCallback = loadingCallback)

            val nextPageKey = response?.meta?.pagination?.run {
                if (current_page < total_pages) {
                    current_page + 1
                }
                else null
            }

            L.i("pageKey: $pageKey nextPageKey: $nextPageKey")

            return LoadResult.Page(
                data = response?.contentData ?: emptyList(),
                prevKey = null,
                nextKey = nextPageKey
            )
        }
        catch (e: IOException) {
            loadingCallback?.onLoadingFailed(e.message)
            loadingCallback?.onLoadingEnd()
            return LoadResult.Error(e)
        }
        catch (e: HttpException) {
            loadingCallback?.onLoadingFailed(e.message)
            loadingCallback?.onLoadingEnd()
            return LoadResult.Error(e)
        }
    }

}