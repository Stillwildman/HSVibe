package com.hsvibe.repositories

import com.hsvibe.model.UserInfo
import com.hsvibe.model.items.*
import com.hsvibe.model.posts.PostUpdateUserInfo

/**
 * Created by Vincent on 2021/7/5.
 */
interface UserRepo : LoadingCallbackRepo {

    suspend fun refreshToken(): Boolean

    suspend fun getUserInfo(): UserInfo?

    suspend fun updateUserInfo(userInfo: UserInfo, lat: String?, lon: String?): UserInfo?

    suspend fun updateUserInfo(postBody: PostUpdateUserInfo): UserInfo?

    suspend fun updatePayPassword(payPassword: String): UserInfo?

    suspend fun updatePassword(password: String): UserInfo?

    suspend fun updateFcmToken(fcmToken: String): UserInfo?

    suspend fun getUserInfoAndUpdate(hasLocationPermission: Boolean): UserInfo?

    suspend fun writeUserInfoToDB(userInfo: UserInfo)

    suspend fun getUserInfoFromDB(): UserInfo?

    suspend fun clearUserInfoFromDB(): Int

    suspend fun getUserBonus(): ItemUserBonus?

    suspend fun getAccountBonus(limit: Int, page: Int): ItemAccountBonus?

    suspend fun getCreditCards(): ItemCardList?

    suspend fun arrangeDefaultCardIndex(cardDetailList: MutableList<ItemCardList.CardData.CardDetail>, key: String? = null)

    suspend fun deleteCreditCard(key: String): ItemCardList?

    suspend fun getPaymentCode(discountAmount: Int, linkKey: String? = null, ticketUuid: String? = null): ItemPayloadCode?

    suspend fun transferPoint(phoneNumber: String, point: Int): ItemPointTransfer?
}