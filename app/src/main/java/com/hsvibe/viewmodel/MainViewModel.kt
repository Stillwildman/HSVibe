package com.hsvibe.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hsvibe.model.Navigation
import com.hsvibe.model.UserInfo
import com.hsvibe.model.items.ItemUserBonus
import com.hsvibe.model.posts.PostUpdateUserInfo
import com.hsvibe.repositories.UserRepo
import com.hsvibe.tasks.ApiStatusException
import com.hsvibe.utilities.L
import com.hsvibe.utilities.Utility
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Vincent on 2021/7/5.
 */
class MainViewModel(private val userRepo: UserRepo) : LoadingStatusViewModel() {

    private fun getExceptionHandler(runIfTokenStatusExpired: () -> Unit): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, throwable ->
            L.i("Handle Coroutine Exception!!!")
            if (!Utility.isNetworkEnabled()) {
                L.e("Network is not working!!!")
            }
            else if (throwable is ApiStatusException && throwable.isTokenStatusExpired()) {
                runIfTokenStatusExpired()
            }
            throwable.printStackTrace()
        }
    }

    private var hasLocationPermission = false

    val liveLocationPermissionChecked by lazy { MutableLiveData<Boolean>() }

    val liveNavigation by lazy { MutableLiveData<Navigation>() }

    val liveUserInfo by lazy { MutableLiveData<UserInfo>() }

    val liveCurrentBalance by lazy { MutableLiveData<ItemUserBonus.ContentData>() }

    val liveUserInfoVisibility = MutableLiveData<Int>()

    init {
        userRepo.setLoadingCallback(this)
    }

    fun setLocationPermissionCheck(hasPermission: Boolean) {
        hasLocationPermission = hasPermission
        liveLocationPermissionChecked.value = true
    }

    fun onNavigating(navigation: Navigation) {
        liveNavigation.value = navigation
    }

    fun onBellClick() {
        liveNavigation.value = Navigation.ClickingBell
    }

    fun onUserNameClick() {
        liveNavigation.value = Navigation.ClickingUserName
    }

    fun isUserInfoNotCompletely(): Boolean {
        return liveUserInfo.value?.let {
            it.getFirstName().isEmpty() || it.getLastName().isEmpty() || it.getBirthday().isEmpty() || it.getGender().isEmpty()
        } ?: true
    }

    fun setUserLoginStatus(isLoggedIn: Boolean) {
        liveUserInfoVisibility.value = if (isLoggedIn) View.VISIBLE  else View.INVISIBLE
    }

    fun setupUserInfoFromDb() {
        viewModelScope.launch {
            userRepo.getUserInfoFromDB()?.let {
                L.i("Setting up UserInfo from DB!!!")
                liveUserInfo.value = it
            } ?: run {
                L.i("UserInfo was null in DB!!!")
            }
        }
    }

    fun runUserInfoSynchronize() {
        viewModelScope.launch(getExceptionHandler {
            refreshUserToken {
                getUserInfoAndUpdate()
            }
        }) {
            getUserInfoAndUpdate()
        }
    }

    private suspend fun getUserInfoAndUpdate() {
        userRepo.getUserInfoAndUpdate(hasLocationPermission)?.let {
            withContext(Dispatchers.Main) {
                liveUserInfo.value = it
                setUserLoginStatus(true)
            }
            userRepo.writeUserInfoToDB(it)
        }
        getUserBonus()
    }

    private fun refreshUserToken(jobAfterRefreshed: suspend () -> Unit) {
        viewModelScope.launch(getExceptionHandler {
            L.e("RefreshToken failed!!!")
        }) {
            userRepo.refreshToken()
            jobAfterRefreshed()
        }
    }

    fun refreshTokenAndUpdate() {
        viewModelScope.launch {
            refreshUserToken { getUserInfoAndUpdate() }
        }
    }

    fun clearUserInfoFromDB() {
        viewModelScope.launch {
            userRepo.clearUserInfoFromDB().also {
                if (it > 0) L.i("UserInfo deleted!!!")
            }
        }
    }

    private fun getUserBonus() {
        viewModelScope.launch {
            userRepo.getUserBonus()?.let {
                liveCurrentBalance.value = it.contentData
            }
        }
    }

    fun updateUserInfo(postBody: PostUpdateUserInfo, onFinish: (isSuccess: Boolean) -> Unit) {
        viewModelScope.launch {
            userRepo.updateUserInfo(postBody)?.let {
                liveUserInfo.postValue(it)
                userRepo.writeUserInfoToDB(it)
                onFinish(true)
            } ?: onFinish(false)
        }
    }
}