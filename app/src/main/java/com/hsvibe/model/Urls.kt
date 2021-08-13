package com.hsvibe.model

/**
 * Created by Vincent on 2021/6/28.
 */
object Urls {

    const val BASE_API_URL = "https://stg-oauth.hsvibe.com/api/v1/code/"

    const val OLD_BASE_API_URL = "https://stg2-api.hsvibe.com/api/v1/"

    const val BASE_SANDBOX_URL = "https://sandbox.hsvibe.com/"

    const val WEB_LOGIN = BASE_SANDBOX_URL + "redirect"

    const val API_REFRESH_TOKEN = BASE_SANDBOX_URL + "api/token/refresh"

    const val API_USER_INFO = "user"

    const val API_CONTENT = "content"

    const val API_COUPON = "coupon"

    const val API_BANNER = "banner"

    const val API_DISTRICTS = OLD_BASE_API_URL + "member/districts"
}