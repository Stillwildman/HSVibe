package com.hsvibe.repositories

import androidx.annotation.StringRes
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.callbacks.OnLoadingCallback
import com.hsvibe.model.ApiConst
import com.hsvibe.model.items.ItemBanner
import com.hsvibe.model.items.ItemContent
import com.hsvibe.model.items.ItemCoupon
import com.hsvibe.model.items.ItemHomeHeader
import com.hsvibe.network.DataCallbacks
import com.hsvibe.utilities.SettingManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Vincent on 2021/7/19.
 */
class HomeContentRepoImpl : HomeContentRepo {

    private var loadingCallback: OnLoadingCallback? = null

    override fun setLoadingCallback(loadingCallback: OnLoadingCallback?) {
        this.loadingCallback = loadingCallback
    }

    override fun getHeaderItemList(): List<ItemHomeHeader> {
        return listOf(
            ItemHomeHeader(getTitleString(R.string.hot_news), true, ApiConst.API_TYPE_NEWS),
            ItemHomeHeader(getTitleString(R.string.hot_coupon), true, ApiConst.API_TYPE_COUPON),
            ItemHomeHeader(getTitleString(R.string.more_discount), false, ApiConst.API_TYPE_DISCOUNT),
            ItemHomeHeader(getTitleString(R.string.hilai_foods), true, ApiConst.API_TYPE_FOODS),
            ItemHomeHeader(getTitleString(R.string.hilai_hotel), true, ApiConst.API_TYPE_HOTEL)
        )
    }

    override suspend fun getNotificationUnreadCount(): Int {
        return withContext(Dispatchers.Default) {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val lastNewestTime = SettingManager.getNewestNotificationTime()

            val notificationItems = DataCallbacks.getContent(
                category = ApiConst.CATEGORY_PERSONAL_NOTIFICATION,
                limit = 10,
                page = 1,
                loadingCallback = loadingCallback)

            var unreadCount = 0

            notificationItems?.contentData?.forEach {
                try {
                    val itemTime: Long = dateFormat.parse(it.approval_at)?.time ?: 0L
                    if (itemTime > lastNewestTime) {
                        unreadCount++
                    }
                }
                catch (e: ParseException) {
                    e.printStackTrace()
                }
            }

            unreadCount
        }
    }

    override suspend fun getNews(): ItemContent? {
        return DataCallbacks.getContent(
            category = ApiConst.CATEGORY_NEWS,
            limit = 5,
            page = 1,
            loadingCallback = loadingCallback)
    }

    override suspend fun getCoupon(): ItemCoupon? {
        return DataCallbacks.getCoupon(
            limit = 5,
            page = 1,
            loadingCallback = loadingCallback)
    }

    override suspend fun getBanner(): ItemBanner? {
        return DataCallbacks.getBanner(loadingCallback)
    }

    private fun getTitleString(@StringRes titleRes: Int): String {
        return AppController.getAppContext().getString(titleRes)
    }
}