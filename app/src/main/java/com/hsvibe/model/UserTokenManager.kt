package com.hsvibe.model

import com.hsvibe.utilities.Extensions.isNotNullOrEmpty
import com.hsvibe.utilities.L
import com.hsvibe.utilities.SettingManager
import com.hsvibe.utilities.Utility

/**
 * Created by Vincent on 2021/6/29.
 */
object UserTokenManager {

    private var userToken: UserToken? = null

    fun setUserToken(userToken: UserToken) {
        userToken.setupCreatedTime()
        SettingManager.setUserToken(userToken)
        this.userToken = userToken
    }

    fun getUserToken(): UserToken? {
        return userToken ?: SettingManager.getUserToken().also { userToken = it }
    }

    fun clearUserToken() {
        SettingManager.clearUserToken()
        userToken = null
    }

    fun hasToken(): Boolean {
        return getUserToken() != null
    }

    fun checkUserTokenStatus(tokenStatusListener: TokenStatusListener) {
        if (userToken == null) {
            userToken = SettingManager.getUserToken()
        }
        userToken.also {
            L.i("CheckUserTokenStatus:\n$it")
        }?.takeIf {
            it.access_token.isNotNullOrEmpty()
        }?.let {
            if (isTokenExpired(it)) {
                tokenStatusListener.onTokenExpired()
            } else {
                tokenStatusListener.onTokenOk()
            }
        } ?: tokenStatusListener.onTokenNull()
    }

    private fun isTokenExpired(userToken: UserToken): Boolean {
        return userToken.run {
            expires_in?.let {
                System.currentTimeMillis() > (createdTime + Utility.convertSecondToMillisecond(it)).also { expiredTime -> L.i("Token expiredTime: $expiredTime") }
            } ?: true
        }
    }

    fun getAuthorization(): String? {
        return userToken?.run { "$token_type $access_token" }
    }

    interface TokenStatusListener {
        fun onTokenOk()
        fun onTokenExpired()
        fun onTokenNull()
    }
}