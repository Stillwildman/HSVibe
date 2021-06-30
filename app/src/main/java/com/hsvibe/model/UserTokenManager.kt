package com.hsvibe.model

import com.hsvibe.utilities.SettingManager
import com.hsvibe.utilities.Utility

/**
 * Created by Vincent on 2021/6/29.
 */
object UserTokenManager {

    const val STATUS_NULL = 0
    const val STATUS_EXPIRED = 1
    const val STATUS_OK = 2

    private var userToken: UserToken? = null

    fun setUserToken(userToken: UserToken) {
        SettingManager.setUserToken(userToken)
        this.userToken = userToken
    }

    fun getUserToken(): UserToken? = userToken

    fun getUserTokenStatus(): Int {
        if (userToken == null) {
            userToken = SettingManager.getUserToken()
        }
        return userToken?.let {
            if (isTokenExpired(it)) {
                STATUS_EXPIRED
            } else {
                STATUS_OK
            }
        } ?: STATUS_NULL
    }

    private fun isTokenExpired(userToken: UserToken): Boolean {
        return userToken.run {
            System.currentTimeMillis() > (createdTime + Utility.convertSecondToMillisecond(expires_in))
        }
    }

    fun getAuthorization(): String? {
        return userToken?.run { "$token_type $access_token" }
    }
}