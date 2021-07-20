package com.hsvibe.repositories

import com.hsvibe.callbacks.OnLoadingCallback
import com.hsvibe.location.MyFusedLocation
import com.hsvibe.model.UserInfo
import com.hsvibe.model.UserInfoManager
import com.hsvibe.model.posts.PostRefreshToken
import com.hsvibe.model.posts.PostUpdateUserInfo
import com.hsvibe.network.DataCallbacks
import com.hsvibe.tasks.TaskController
import com.hsvibe.utilities.DeviceUtil
import com.hsvibe.utilities.L
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Created by Vincent on 2021/7/5.
 */
class UserRepoImpl : UserRepo {

    companion object {
        private const val TASK_KEY_GET_USER_INFO = 0
        private const val TASK_KEY_UPDATE_USER_INFO = 1
        private const val TASK_KEY_GET_AND_UPDATE_USER_INFO = 2
    }

    private var callback: OnLoadingCallback? = null

    private val taskController by lazy { TaskController<UserInfo?>() }

    override fun setLoadingCallback(loadingCallback: OnLoadingCallback?) {
        this.callback = loadingCallback
    }

    override suspend fun refreshToken() {
        UserInfoManager.getUserToken()?.refresh_token?.let {
            val userToken = DataCallbacks.refreshUserToken(PostRefreshToken(it), callback)
            userToken?.run {
                UserInfoManager.setUserToken(this)
            }
        }
    }

    override suspend fun getUserInfo(): UserInfo? {
        return taskController.joinPreviousOrRun(TASK_KEY_GET_USER_INFO) {
            UserInfoManager.getAuthorization()?.let {
                DataCallbacks.getUserInfo(it, callback)?.also { userInfo ->
                    UserInfoManager.setUserInfo(userInfo)
                }
            }
        }
    }

    override suspend fun updateUserInfo(userInfo: UserInfo, lat: String, lon: String): UserInfo? {
        return taskController.joinPreviousOrRun(TASK_KEY_UPDATE_USER_INFO) {
            UserInfoManager.getAuthorization()?.let {
                val postBody = getUserInfoUpdateBody(userInfo, lat, lon)
                DataCallbacks.updateUserInfo(it, postBody, callback)?.also { updatedUserInfo ->
                    L.i("Update UserInfo: ${updatedUserInfo.getMobileNumber()} Device: ${updatedUserInfo.getDeviceModel()}")
                    UserInfoManager.setUserInfo(userInfo)
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    override suspend fun getUserInfoAndUpdate(): UserInfo? {
        return taskController.joinPreviousOrRun(TASK_KEY_GET_AND_UPDATE_USER_INFO) {
            val userInfo = getUserInfo()
            L.i("Get UserInfo: ${userInfo?.getFirstName()} ${userInfo?.getLastName()}")

            val location = MyFusedLocation.awaitLastLocation()
            L.i("Get Location: ${location.latitude} , ${location.longitude}")

            userInfo?.let {
                updateUserInfo(it, location.latitude.toString(), location.longitude.toString())
            }
        }
    }

    private fun getUserInfoUpdateBody(userInfo: UserInfo, lat: String, lon: String): PostUpdateUserInfo {
        return PostUpdateUserInfo(
            userInfo.getFirstName(),
            userInfo.getLastName(),
            userInfo.getMobileNumber(),
            userInfo.getGender(),
            userInfo.getBirthday(),
            DeviceUtil.getDeviceType(),
            DeviceUtil.getCombinedDeviceModel(),
            lat,
            lon
        )
    }

    override suspend fun writeUserInfoToDB(userInfo: UserInfo) {
        // TODO
    }
}