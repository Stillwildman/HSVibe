package com.hsvibe.model

/**
 * Created by Vincent on 2021/7/14.
 */
object ApiConst {

    const val AUTHORIZATION = "Authorization"

    // Common Queries
    const val SEARCH = "search"
    const val ORDER_BY = "orderBy"
    const val SORTED_BY = "sortedBy"
    const val LIMIT = "limit"
    const val PAGE = "page"
    const val CATEGORY_ID = "category_id"
    const val STORE_ID = "store_id"
    const val PARTNER_ID = "partner_id"

    // Common Params
    const val ORDER_BY_TOP = "is_top"
    const val ORDER_BY_UPDATED = "updated_at"
    const val SORTED_BY_ASC = "asc"
    const val SORTED_BY_DESC = "desc"

    // Returned message
    const val SUCCESS = "success"

    // Returned keys
    const val STATUS = "status"
    const val MESSAGE = "message"

    // PARTNER ID
    const val PARTNER_FOODS = 1
    const val PARTNER_HOTELS = 2

    // Content Category Params
    const val CATEGORY_ANNOUNCEMENT = 4
    const val CATEGORY_NEWS = 5
    const val CATEGORY_PERSONAL_NOTIFICATION = 6
    const val CATEGORY_EXPLORE = 7

    // Coupon Category Params
    const val MY_COUPONS = -1
    const val ALL = 0
    const val CATEGORY_DISTRICTS = 2

    const val COUPON_STATUS_NOT_USED = 0
    const val COUPON_STATUS_USED = 1
    const val COUPON_STATUS_EXPIRED = 2

    const val DEFAULT_LIMIT = 10 // Server default is 15, but I prefer 10!

    const val API_TYPE_NEWS = 0
    const val API_TYPE_COUPON = 1
    const val API_TYPE_DISCOUNT = 2
    const val API_TYPE_FOODS = 3
    const val API_TYPE_HOTEL = 4

    // Credit Card
    const val CARD_CODE_SUCCESS = "00"
}