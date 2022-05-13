package com.hsvibe.model

import com.hsvibe.model.items.ItemBanner
import com.hsvibe.model.items.ItemCoupon

/**
 * Created by Vincent on 2021/8/2.
 */
sealed class Navigation {
    class ClickingMore(val apiType: Int) : Navigation()
    class ClickingNews(val itemIndex: Int) : Navigation()
    class ClickingCoupon(val couponItem: ItemCoupon.ContentData) : Navigation()
    class ClickingBanner(val bannerItem: ItemBanner.ContentData) : Navigation()
    object ClickingBell : Navigation()
    object ClickPaymentCard : Navigation()
    object ClickingUserName : Navigation()
    object OnAuthorizationFailed : Navigation()
    object OnLoginRequired : Navigation()
}
