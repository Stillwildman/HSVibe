package com.hsvibe.repositories

import com.hsvibe.model.items.ItemBanner
import com.hsvibe.model.items.ItemContent
import com.hsvibe.model.items.ItemCoupon
import com.hsvibe.model.items.ItemHomeHeader

/**
 * Created by Vincent on 2021/7/19.
 */
interface HomeContentRepo : LoadingCallbackRepo {

    fun getHeaderItemList(): List<ItemHomeHeader>

    suspend fun getNotificationUnreadCount(): Int

    suspend fun getNews(): ItemContent?

    suspend fun getCoupon(): ItemCoupon?

    suspend fun getBanner(): ItemBanner?

    suspend fun getHilaiFoods(): ItemCoupon?

    suspend fun getHilaiHotels(): ItemCoupon?
}