package com.hsvibe.model

import com.hsvibe.model.items.ItemBanner
import com.hsvibe.model.items.ItemContent
import com.hsvibe.model.items.ItemCoupon

/**
 * Created by Vincent on 2021/8/2.
 */
sealed class Navigation {
    class ClickingMore(val apiType: Int): Navigation()
    class ClickingNews(val newsItem: ItemContent.ContentData?): Navigation()
    class ClickingCoupon(val couponItem: ItemCoupon.ContentData?): Navigation()
    class ClickingBanner(val bannerItem: ItemBanner.ContentData?): Navigation()
}
