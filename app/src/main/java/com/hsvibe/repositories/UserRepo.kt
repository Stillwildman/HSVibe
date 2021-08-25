package com.hsvibe.repositories

import com.hsvibe.callbacks.OnLoadingCallback
import com.hsvibe.model.UserInfo
import com.hsvibe.model.items.ItemAccountBonus
import com.hsvibe.model.items.ItemUserBonus
import com.hsvibe.model.posts.PostUpdateUserInfo

/**
 * Created by Vincent on 2021/7/5.
 */
interface UserRepo {

    fun setLoadingCallback(loadingCallback: OnLoadingCallback?)

    suspend fun refreshToken()

    suspend fun getUserInfo(): UserInfo?

    suspend fun updateUserInfo(userInfo: UserInfo, lat: String?, lon: String?): UserInfo?

    suspend fun updateUserInfo(postBody: PostUpdateUserInfo): UserInfo?

    suspend fun updatePayPassword(payPassword: String): UserInfo?

    suspend fun updateFcmToken(fcmToken: String): UserInfo?

    suspend fun getUserInfoAndUpdate(hasLocationPermission: Boolean): UserInfo?

    suspend fun writeUserInfoToDB(userInfo: UserInfo)

    suspend fun getUserInfoFromDB(): UserInfo?

    suspend fun clearUserInfoFromDB(): Int

    suspend fun getUserBonus(): ItemUserBonus?

    suspend fun getAccountBonus(limit: Int, page: Int): ItemAccountBonus?
}