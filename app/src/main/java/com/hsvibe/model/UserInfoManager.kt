package com.hsvibe.model

import com.hsvibe.utilities.L
import com.hsvibe.utilities.SettingManager
import com.hsvibe.utilities.Utility

/**
 * Created by Vincent on 2021/6/29.
 */
object UserInfoManager {

    const val STATUS_NULL = 0
    const val STATUS_EXPIRED = 1
    const val STATUS_OK = 2

    private var userToken: UserToken? = null
    private var userInfo: UserInfo? = null

    fun setUserToken(userToken: UserToken) {
        userToken.setupCreatedTime()
        SettingManager.setUserToken(userToken)
        this.userToken = userToken
    }

    fun getUserToken(): UserToken? {
        return userToken ?: SettingManager.getUserToken().also { userToken = it }
    }

    fun setUserInfo(userInfo: UserInfo) {
        this.userInfo = userInfo
    }

    fun isUserInfoNotCompletely(): Boolean {
        return userInfo?.let {
            it.getFirstName().isEmpty() || it.getLastName().isEmpty() || it.getBirthday().isEmpty() || it.getGender().isEmpty()
        } ?: true
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
            System.currentTimeMillis() > (createdTime + Utility.convertSecondToMillisecond(expires_in))
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