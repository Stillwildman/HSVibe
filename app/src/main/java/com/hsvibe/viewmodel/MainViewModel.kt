package com.hsvibe.viewmodel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.model.Navigation
import com.hsvibe.model.UserInfo
import com.hsvibe.model.items.ItemCardList
import com.hsvibe.model.items.ItemUserBonus
import com.hsvibe.model.posts.PostUpdateUserInfo
import com.hsvibe.repositories.UserRepo
import com.hsvibe.tasks.ApiStatusException
import com.hsvibe.utilities.*
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

/**
 * Created by Vincent on 2021/7/5.
 */
class MainViewModel(private val userRepo: UserRepo) : LoadingStatusViewModel() {

    private fun getExceptionHandler(runIfTokenStatusExpired: () -> Unit): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, throwable ->
            L.e("Handle Coroutine Exception!!!")
            when {
                !Utility.isNetworkEnabled() -> {
                    L.e("Network is not working!!!")
                    Utility.toastLong("Network is not working!!!")
                    onLoadingEnd()
                }
                throwable is ApiStatusException && throwable.isTokenStatusExpired() -> {
                    runIfTokenStatusExpired()
                }
                throwable is ApiStatusException -> {
                    Utility.toastLong("Api Error!!\nCode: ${throwable.statusCode}\nMsg: ${throwable.errorBody}")
                    onLoadingEnd()
                }
                else -> {
                    Utility.toastLong(R.string.unknown_network_error)
                    onLoadingEnd()
                }
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

    val liveCreditCards by lazy { MutableLiveData<ItemCardList>() }

    val livePasswordVerified: LiveData<Boolean>
        get() = _passwordVerified

    private val _passwordVerified by lazy { SingleLiveEvent<Boolean>() }

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
        updateFcmToken()
    }

    private fun refreshUserToken(jobAfterRefreshed: suspend () -> Unit) {
        viewModelScope.launch(getExceptionHandler {
            L.e("RefreshToken failed!!!")
            liveNavigation.value = Navigation.OnAuthorizationFailed
        }) {
            if (userRepo.refreshToken()) {
                jobAfterRefreshed()
            }
            else {
                liveNavigation.value = Navigation.OnAuthorizationFailed
            }
        }
    }

    fun refreshTokenAndUpdate() {
        refreshUserToken { getUserInfoAndUpdate() }
    }



    private fun updateFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result.let {
                    viewModelScope.launch {
                        userRepo.updateFcmToken(it)
                    }
                    L.i("FCM Token Get!!! $it")
                }
            }
            else {
                L.e("Retrieve FCM Token Failed!!! ${task.exception}")
            }
        }
    }

    fun clearUserInfoFromDB() {
        viewModelScope.launch {
            userRepo.clearUserInfoFromDB().also {
                if (it > 0) L.i("UserInfo deleted!!!")
            }
        }
    }

    fun getUserBonus() {
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

    fun updatePayPassword(payPassword: String, onFinish: (isSuccess: Boolean) -> Unit) {
        viewModelScope.launch {
            userRepo.updatePayPassword(payPassword)?.let {
                liveUserInfo.postValue(it)
                onFinish(true)
            } ?: onFinish(false)
        }
    }

    fun updatePassword(password: String, onFinish: (isSuccess: Boolean) -> Unit) {
        viewModelScope.launch {
            userRepo.updatePassword(password)?.let {
                liveUserInfo.postValue(it)
                onFinish(true)
            } ?: onFinish(false)
        }
    }

    fun getCurrentUserBonus(): ItemUserBonus.ContentData? {
        return liveCurrentBalance.value
    }

    fun requireLogin() {
        liveNavigation.value = Navigation.OnLoginRequired
    }

    fun getExpiringPointText(): String {
        return liveUserInfo.value?.getExpiringPoint().takeIf { it.isNotNullOrEmpty() }?.let {
            val currentYear = Calendar.getInstance().get(Calendar.YEAR)
            AppController.getAppContext().getString(R.string.your_points_and_expires_time, it, currentYear)
        } ?: ""
    }

    fun setPasswordVerified(isVerified: Boolean?) {
        _passwordVerified.value = isVerified
    }

    fun isPasswordVerified(): Boolean {
        return livePasswordVerified.value == true
    }

    fun hasSetPayPassword(): Boolean {
        return liveUserInfo.value?.isSetPassword() == true
    }

    fun loadCreditCards() {
        viewModelScope.launch(getExceptionHandler()) {
            userRepo.getCreditCards()?.let {
                liveCreditCards.value = it
            }
        }
    }

    fun updateDefaultCreditCardIndex(key: String) {
        viewModelScope.launch {
            liveCreditCards.value?.cardData?.cardDetailList?.let {
                userRepo.arrangeDefaultCardIndex(it, key)
                liveCreditCards.forceRefresh()
            }
        }
    }
}