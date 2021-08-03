package com.hsvibe.viewmodel

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hsvibe.callbacks.OnLoadingCallback
import com.hsvibe.model.Navigation
import com.hsvibe.model.UserInfo
import com.hsvibe.repositories.UserRepo
import com.hsvibe.tasks.ApiStatusException
import com.hsvibe.utilities.L
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Vincent on 2021/7/5.
 */
class MainViewModel(private val userRepo: UserRepo) : ViewModel(), OnLoadingCallback {

    private fun getExceptionHandler(runIfTokenStatusExpired: () -> Unit): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, throwable ->
            L.i("Handle Coroutine Exception!!!")
            if (throwable is ApiStatusException && throwable.isTokenStatusExpired()) {
                runIfTokenStatusExpired()
            }
        }
    }

    val liveNavigation by lazy { MutableLiveData<Navigation>() }

    val liveLoadingStatus = MutableLiveData<Boolean>()
    val liveErrorMessage = MutableLiveData<String>()

    val liveUserInfo = MutableLiveData<UserInfo>()

    val liveUserInfoVisibility = MutableLiveData<Int>()

    init {
        userRepo.setLoadingCallback(this)
    }

    override fun onLoadingStart() {
        liveLoadingStatus.postValue(true)
    }

    override fun onLoadingEnd() {
        liveLoadingStatus.postValue(false)
    }

    override fun onLoadingFailed(errorMessage: String?) {
        liveErrorMessage.postValue(errorMessage ?: "")
    }

    fun onNavigating(navigation: Navigation) {
        liveNavigation.value = navigation
    }

    fun setUserLoginStatus(isLoggedIn: Boolean) {
        liveUserInfoVisibility.value = if (isLoggedIn) View.VISIBLE  else View.INVISIBLE
    }

    fun runUserInfoUpdating() {
        viewModelScope.launch(getExceptionHandler {
            refreshUserToken()
        }) {
            getUserInfoAndUpdate()
        }
    }

    private suspend fun getUserInfoAndUpdate() {
        userRepo.getUserInfoAndUpdate()?.let {
            withContext(Dispatchers.Main) {
                liveUserInfo.value = it
                setUserLoginStatus(true)
            }
            userRepo.writeUserInfoToDB(it)
        }
    }

    fun refreshUserToken() {
        viewModelScope.launch(getExceptionHandler {
            L.e("RefreshToken failed!!!")
        }) {
            userRepo.refreshToken()
            getUserInfoAndUpdate()
        }
    }

    private fun writeUserInfoAndCheck(userInfo: UserInfo) {

    }
}