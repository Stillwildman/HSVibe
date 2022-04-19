package com.hsvibe.model

/**
 * Created by Vincent on 2021/6/28.
 */
object Urls {

    const val BASE_API_URL = "https://stg-oauth.hsvibe.com/api/v1/code/"

    private const val OLD_BASE_API_URL = "https://stg2-api.hsvibe.com/api/v1/"

    private const val BASE_SANDBOX_URL = "https://sandbox.hsvibe.com/"

    const val WEB_LOGIN = BASE_SANDBOX_URL + "redirect"

    const val API_REFRESH_TOKEN = BASE_SANDBOX_URL + "api/token/refresh"

    const val API_USER_INFO = "user"

    const val API_CONTENT = "content"

    const val API_COUPON = "coupon"

    const val API_BANNER = "banner"

    const val API_USER_BONUS = "user/bonus"

    const val API_ACCOUNT_BONUS = "account/bonus"

    const val API_TICKET_HOLDER = "ticketholder"

    const val API_COUPON_REDEEM = "ticketholder/bonus/expenses"

    const val API_COUPON_USE = "transaction/payload"

    const val API_DISTRICTS = OLD_BASE_API_URL + "member/districts" // TODO Need to be replaced with new API

    const val API_COUPON_CATEGORY = "category"

    const val API_COUPON_STORES = "store"

    const val API_COUPON_BRAND = "brand/coupon"

    const val WEB_HS_VIBE = "https://www.hsvibe.com/"

    const val WEB_HS_VIBE_TERMS = "https://www.hsvibe.com/terms"

    const val WEB_HS_VIBE_PRIVACY = "https://www.hsvibe.com/privacy"
}