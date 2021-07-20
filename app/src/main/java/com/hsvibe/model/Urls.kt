package com.hsvibe.model

/**
 * Created by Vincent on 2021/6/28.
 */
object Urls {

    const val BASE_API_URL = "https://stg-oauth.hsvibe.com/api/v1/code/"

    const val BASE_SANDBOX_URL = "https://sandbox.hsvibe.com/"

    const val WEB_LOGIN = BASE_SANDBOX_URL + "redirect"

    const val API_REFRESH_TOKEN = BASE_SANDBOX_URL + "api/token/refresh"

    const val API_USER_INFO = "user"

    const val API_CONTENT = "content"

    const val API_COUPON = "coupon"
}