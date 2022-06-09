package com.hsvibe.model

import com.hsvibe.BuildConfig

/**
 * Created by Vincent on 2021/6/28.
 */
object Urls {

    val BASE_API_URL = if (BuildConfig.IS_FORMAL_ENV) "https://oauth.hsvibe.com/api/v1/code/" else "https://stg-oauth.hsvibe.com/api/v1/code/"

    private const val OLD_BASE_API_URL = "https://stg2-api.hsvibe.com/api/v1/"

    private val BASE_SANDBOX_URL = if (BuildConfig.IS_FORMAL_ENV) "https://login.hsvibe.com/" else "https://sandbox.hsvibe.com/"

    val WEB_LOGIN = BASE_SANDBOX_URL + "redirect"

    val API_REFRESH_TOKEN = BASE_SANDBOX_URL + "api/token/refresh"

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

    const val API_COUPON_BRAND = "brand/coupon"

    const val API_VERIFY_PAY_PASSWORD = "user/verifyPayPassword"

    const val API_CARD_LINK = "payment/cardlink"

    const val API_CARD_LINK_DELETE = "payment/cardlink/cancel"

    const val API_PAYMENT_PAYLOAD = "transaction/payload"

    const val API_TRANSACTION_HISTORY = "transaction/payment"

    const val API_TRANSFER_POINT = "account/bonus/transfer"

    const val WEB_HS_VIBE = "https://www.hsvibe.com/"

    const val WEB_HS_VIBE_TERMS = "https://www.hsvibe.com/terms"

    const val WEB_HS_VIBE_PRIVACY = "https://www.hsvibe.com/privacy"

    const val WEB_REGISTER_CREDIT_CARD = "https://stg-oauth.hsvibe.com/payment/esunbank/cardlink/register/{0}"

    const val HSVIBE_DYNAMIC_LINK = "https://hsvibe.page.link"

    const val HSVIBE_DYNAMIC_LINK_FALLBACK = "https://www.hsvibe.com/dynamiclinks"
}