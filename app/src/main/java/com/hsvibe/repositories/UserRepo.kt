package com.hsvibe.repositories

import com.hsvibe.callbacks.OnLoadingCallback
import com.hsvibe.model.UserInfo

/**
 * Created by Vincent on 2021/7/5.
 */
interface UserRepo {

    fun setLoadingCallback(loadingCallback: OnLoadingCallback?)

    suspend fun refreshToken()

    suspend fun getUserInfo(): UserInfo?

    suspend fun updateUserInfo(userInfo: UserInfo, lat: String, lon: String): UserInfo?

    suspend fun updatePayPassword(payPassword: String): UserInfo?

    suspend fun getUserInfoAndUpdate(): UserInfo?

    suspend fun writeUserInfoToDB(userInfo: UserInfo)

    suspend fun getUserInfoFromDB(): UserInfo?

    suspend fun clearUserInfoFromDB(): Int

}