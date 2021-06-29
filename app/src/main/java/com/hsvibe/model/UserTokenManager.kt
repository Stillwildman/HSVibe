package com.hsvibe.model

import com.hsvibe.utilities.SettingManager

/**
 * Created by Vincent on 2021/6/29.
 */
object UserTokenManager {

    private var userToken: UserToken? = null

    fun setUserToken(userToken: UserToken) {
        this.userToken = userToken
    }

    fun getUserToken(): UserToken? = userToken

    fun hasUserToken(): Boolean {
        return SettingManager.getUserToken()?.run {
            userToken = this
            true
        } ?: false
    }

    fun isTokenExpired(): Boolean {
        return userToken?.run { System.currentTimeMillis() - acquiredTime > Const.TOKEN_EXPIRED_DURATION } ?: true
    }

    fun getAuthorization(): String? {
        return userToken?.run { "$token_type $access_token" }
    }
}