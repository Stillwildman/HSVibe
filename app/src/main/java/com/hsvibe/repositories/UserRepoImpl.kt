package com.hsvibe.repositories

import android.location.Location
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.callbacks.OnLoadingCallback
import com.hsvibe.database.UserDatabase
import com.hsvibe.location.MyFusedLocation
import com.hsvibe.model.ApiConst
import com.hsvibe.model.Const
import com.hsvibe.model.UserInfo
import com.hsvibe.model.UserTokenManager
import com.hsvibe.model.entities.UserInfoEntity
import com.hsvibe.model.items.*
import com.hsvibe.model.posts.PostPaymentPayload
import com.hsvibe.model.posts.PostRefreshToken
import com.hsvibe.model.posts.PostTransferPoint
import com.hsvibe.model.posts.PostUpdateUserInfo
import com.hsvibe.network.DataCallbacks
import com.hsvibe.tasks.TaskController
import com.hsvibe.utilities.DeviceUtil
import com.hsvibe.utilities.L
import com.hsvibe.utilities.SettingManager
import com.hsvibe.utilities.isNotNullOrEmpty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Created by Vincent on 2021/7/5.
 */
class UserRepoImpl : UserRepo {

    private var callback: OnLoadingCallback? = null

    private val taskController by lazy { TaskController<UserInfo?>() }

    override fun setLoadingCallback(loadingCallback: OnLoadingCallback?) {
        this.callback = loadingCallback
    }

    override suspend fun refreshToken(): Boolean {
        return UserTokenManager.getUserToken()?.refresh_token?.let { refreshToken ->
            val userToken = DataCallbacks.refreshUserToken(PostRefreshToken(refreshToken), callback)
            userToken?.takeIf { it.access_token.isNotNullOrEmpty() }?.run {
                UserTokenManager.setUserToken(this)
                true
            } ?: false
        } ?: false
    }

    override suspend fun getUserInfo(): UserInfo? {
        return taskController.joinPreviousOrRun(TaskController.KEY_GET_USER_INFO) {
            UserTokenManager.getAuthorization()?.let {
                DataCallbacks.getUserInfo(it, callback)
            }
        }
    }

    override suspend fun updateUserInfo(userInfo: UserInfo, lat: String?, lon: String?): UserInfo? {
        return taskController.joinPreviousOrRun(TaskController.KEY_UPDATE_USER_INFO) {
            UserTokenManager.getAuthorization()?.let {
                val postBody = getUserInfoUpdateBody(userInfo, lat, lon)
                DataCallbacks.updateUserInfo(it, postBody, callback)?.also { updatedUserInfo ->
                    L.i("Update UserInfo: ${updatedUserInfo.getMobileNumber()} Device: ${updatedUserInfo.getDeviceModel()}")
                }
            }
        }
    }

    override suspend fun updateUserInfo(postBody: PostUpdateUserInfo): UserInfo? {
        return taskController.joinPreviousOrRun(TaskController.KEY_UPDATE_USER_INFO) {
            UserTokenManager.getAuthorization()?.let {

                correctPostBodyIfNeed(postBody)

                DataCallbacks.updateUserInfo(it, postBody, callback)?.also { updatedUserInfo ->
                    L.i("Update UserInfo: ${updatedUserInfo.getMobileNumber()} Device: ${updatedUserInfo.getDeviceModel()}")
                }
            }
        }
    }

    override suspend fun updatePayPassword(payPassword: String): UserInfo? {
        return taskController.joinPreviousOrRun(TaskController.KEY_UPDATE_PAY_PASSWORD) {
            UserTokenManager.getAuthorization()?.let {
                DataCallbacks.updateUserInfo(it, PostUpdateUserInfo(pay_password = payPassword), callback)
            }
        }
    }

    override suspend fun updatePassword(password: String): UserInfo? {
        return taskController.joinPreviousOrRun(TaskController.KEY_UPDATE_PASSWORD) {
            UserTokenManager.getAuthorization()?.let {
                DataCallbacks.updateUserInfo(it, PostUpdateUserInfo(password = password), callback)
            }
        }
    }

    override suspend fun updateFcmToken(fcmToken: String): UserInfo? {
        return taskController.joinPreviousOrRun(TaskController.KEY_UPDATE_FCM_TOKEN) {
            UserTokenManager.getAuthorization()?.let {
                DataCallbacks.updateUserInfo(it, PostUpdateUserInfo(fcm_token = fcmToken), callback)
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getUserInfoAndUpdate(hasLocationPermission: Boolean): UserInfo? {
        return taskController.joinPreviousOrRun(TaskController.KEY_GET_AND_UPDATE_USER_INFO) {
            val userInfo = getUserInfo()
            L.i("Get UserInfo: ${userInfo?.getFirstName()} ${userInfo?.getLastName()}")

            val location: Location? = if (hasLocationPermission) {
                MyFusedLocation.awaitLastLocation().also {
                    L.i("Get Location: ${it?.latitude} , ${it?.longitude}")
                }
            } else null

            userInfo?.let {
                updateUserInfo(it, location?.latitude?.toString(), location?.longitude?.toString()) ?: it
            }
        }
    }

    private fun getUserInfoUpdateBody(userInfo: UserInfo, lat: String?, lon: String?): PostUpdateUserInfo {
        return PostUpdateUserInfo(
            first_name = userInfo.getFirstName().takeIf { it.isNotEmpty() },
            last_name = userInfo.getLastName().takeIf { it.isNotEmpty() },
            mobile_number = userInfo.getMobileNumber(),
            gender = userInfo.getGender(),
            birthday = userInfo.getBirthday(),
            device_type = DeviceUtil.getDeviceType(),
            device_model = DeviceUtil.getCombinedDeviceModel(),
            region_zip = userInfo.getRegionZip(),
            referrer_no = userInfo.getReferrerNo().takeIf { it.length >= Const.REFERRER_NUMBER_LENGTH_LIMIT },
            lat = lat,
            long = lon
        )
    }

    private fun correctPostBodyIfNeed(postBody: PostUpdateUserInfo) {
        postBody.apply {
            if (gender == AppController.getString(R.string.gender_other_eng)) {
                gender = null
            }
            if ((referrer_no?.length ?: 0) < Const.REFERRER_NUMBER_LENGTH_LIMIT) {
                referrer_no = null
            }
        }
    }

    override suspend fun writeUserInfoToDB(userInfo: UserInfo) {
        withContext(Dispatchers.IO) {
            val insertedId = UserDatabase.getInstance().getUserInfoDao().insertUserInfo(UserInfoEntity(userInfo))
            L.i("Write UserInfo Info DB!!! InsertedID: $insertedId")
        }
    }

    override suspend fun getUserInfoFromDB(): UserInfo? {
        return withContext(Dispatchers.IO) {
            UserDatabase.getInstance().getUserInfoDao().getUserInfo()
        }
    }

    override suspend fun clearUserInfoFromDB(): Int {
        return withContext(Dispatchers.IO) {
            UserDatabase.getInstance().getUserInfoDao().clearUserInfo()
        }
    }

    override suspend fun getUserBonus(): ItemUserBonus? {
        return UserTokenManager.getAuthorization()?.let {
            DataCallbacks.getUserBonus(it, callback)
        }
    }

    override suspend fun getAccountBonus(limit: Int, page: Int): ItemAccountBonus? {
        return UserTokenManager.getAuthorization()?.let {
            DataCallbacks.getAccountBonus(it, limit, page, callback)
        }
    }

    override suspend fun getCreditCards(): ItemCardList? {
        return UserTokenManager.getAuthorization()?.let {
            DataCallbacks.getCreditCards(it, callback)?.also { cardListItem ->
                arrangeDefaultCardIndex(cardListItem.cardData.cardDetailList)
            }
        }
    }

    override suspend fun arrangeDefaultCardIndex(cardDetailList: MutableList<ItemCardList.CardData.CardDetail>, key: String?) {
        withContext(Dispatchers.Default) {
            val defaultKey = key ?: SettingManager.getDefaultCreditCardKey()
            var defaultCardIndex = 0

            defaultKey?.let {
                cardDetailList.forEachIndexed { index, cardDetail ->
                    if (cardDetail.key == defaultKey) {
                        defaultCardIndex = index
                    } else {
                        cardDetail.isDefault = false
                    }
                }
            }
            if (cardDetailList.size > 0) {
                cardDetailList[defaultCardIndex].isDefault = true

                if (defaultCardIndex != 0) {
                    Collections.swap(cardDetailList, 0, defaultCardIndex)
                }
                if (defaultKey == null) {
                    SettingManager.setDefaultCreditCardKey(cardDetailList[defaultCardIndex].key)
                }
            }
        }
    }

    override suspend fun deleteCreditCard(key: String): ItemCardList? {
        return UserTokenManager.getAuthorization()?.let {
            DataCallbacks.deleteCreditCard(it, key, callback)
        }
    }

    override suspend fun getPaymentCode(discountAmount: Int, linkKey: String?, ticketUuid: String?): ItemPayloadCode? {
        return UserTokenManager.getAuthorization()?.let {
            DataCallbacks.getPaymentCode(it, PostPaymentPayload(discountAmount, linkKey, ticketUuid), callback)
        }
    }

    override suspend fun transferPoint(phoneNumber: String, point: Int): ItemPointTransfer? {
        return UserTokenManager.getAuthorization()?.let {
            DataCallbacks.transferPoint(it, PostTransferPoint(phoneNumber, point, AppController.getString(R.string.member_point_transfer)), callback)
        }
    }

    override suspend fun findNewsIndex(newsUuid: String): Int {
        val newsList = DataCallbacks.getContent(
            category = ApiConst.CATEGORY_PERSONAL_NOTIFICATION,
            limit = 50,
            page = 1,
            loadingCallback = callback)

        var newsIndex = 0

        newsList?.contentData?.forEachIndexed loop@ { index, contentData ->
            if (contentData.uuid == newsUuid) {
                newsIndex = index
                return@loop
            }
        }
        return newsIndex
    }
}