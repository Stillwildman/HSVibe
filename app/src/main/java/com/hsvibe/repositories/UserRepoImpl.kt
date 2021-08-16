package com.hsvibe.repositories

import android.location.Location
import com.hsvibe.callbacks.OnLoadingCallback
import com.hsvibe.database.UserDatabase
import com.hsvibe.location.MyFusedLocation
import com.hsvibe.model.UserInfo
import com.hsvibe.model.UserTokenManager
import com.hsvibe.model.entities.UserInfoEntity
import com.hsvibe.model.items.ItemAccountBonus
import com.hsvibe.model.items.ItemUserBonus
import com.hsvibe.model.posts.PostRefreshToken
import com.hsvibe.model.posts.PostUpdateUserInfo
import com.hsvibe.network.DataCallbacks
import com.hsvibe.tasks.TaskController
import com.hsvibe.utilities.DeviceUtil
import com.hsvibe.utilities.L
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext

/**
 * Created by Vincent on 2021/7/5.
 */
class UserRepoImpl : UserRepo {

    private var callback: OnLoadingCallback? = null

    private val taskController by lazy { TaskController<UserInfo?>() }

    override fun setLoadingCallback(loadingCallback: OnLoadingCallback?) {
        this.callback = loadingCallback
    }

    override suspend fun refreshToken() {
        UserTokenManager.getUserToken()?.refresh_token?.let {
            val userToken = DataCallbacks.refreshUserToken(PostRefreshToken(it), callback)
            userToken?.run {
                UserTokenManager.setUserToken(this)
            }
        }
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

    override suspend fun updateFcmToken(fcmToken: String): UserInfo? {
        return taskController.joinPreviousOrRun(TaskController.KEY_UPDATE_FCM_TOKEN) {
            UserTokenManager.getAuthorization()?.let {
                DataCallbacks.updateUserInfo(it, PostUpdateUserInfo(fcm_token = fcmToken), callback)
            }
        }
    }

    @ExperimentalCoroutinesApi
    override suspend fun getUserInfoAndUpdate(hasLocationPermission: Boolean): UserInfo? {
        return taskController.joinPreviousOrRun(TaskController.KEY_GET_AND_UPDATE_USER_INFO) {
            val userInfo = getUserInfo()
            L.i("Get UserInfo: ${userInfo?.getFirstName()} ${userInfo?.getLastName()}")

            val location: Location? = if (hasLocationPermission) {
                MyFusedLocation.awaitLastLocation().also {
                    L.i("Get Location: ${it.latitude} , ${it.longitude}")
                }
            } else null

            userInfo?.let {
                updateUserInfo(it, location?.latitude?.toString(), location?.longitude?.toString()) ?: it
            }
        }
    }

    private fun getUserInfoUpdateBody(userInfo: UserInfo, lat: String?, lon: String?): PostUpdateUserInfo {
        return PostUpdateUserInfo(
            first_name = userInfo.getFirstName(),
            last_name = userInfo.getLastName(),
            mobile_number = userInfo.getMobileNumber(),
            gender = userInfo.getGender(),
            birthday = userInfo.getBirthday(),
            device_type = DeviceUtil.getDeviceType(),
            device_model = DeviceUtil.getCombinedDeviceModel(),
            referrer_no = userInfo.getReferrerNo(),
            lat = lat,
            long = lon
        )
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
}