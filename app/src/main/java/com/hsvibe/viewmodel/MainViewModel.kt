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
import com.hsvibe.model.items.ItemPaymentDisplay
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

    companion object {
        private const val TAG = "MainViewModel"
    }

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

    val liveCreditCardDeleting by lazy { SingleLiveEvent<ItemCardList>() }

    val livePasswordVerified: LiveData<Boolean>
        get() = _passwordVerified

    private val _passwordVerified by lazy { SingleLiveEvent<Boolean>() }

    val livePaymentDisplay by lazy { MutableLiveData<ItemPaymentDisplay>() }

    var isCouponSelectingMode = false

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
                L.i(TAG, "Setting up UserInfo from DB!!!")
                liveUserInfo.value = it
            } ?: run {
                L.i(TAG, "UserInfo was null in DB!!!")
            }
        }
    }

    fun runUserInfoSynchronize() {
        viewModelScope.launch(getExceptionHandler {
            refreshUserToken {
                loadUserInfoAndUpdate()
            }
        }) {
            loadUserInfoAndUpdate()
            loadUserBonus()
            updateFcmToken()
        }
    }

    private suspend fun loadUserInfoAndUpdate() {
        L.i(TAG, "getUserInfoAndUpdate!!!")
        userRepo.getUserInfoAndUpdate(hasLocationPermission)?.let {
            withContext(Dispatchers.Main) {
                liveUserInfo.value = it
                setUserLoginStatus(true)
            }
            userRepo.writeUserInfoToDB(it)
        }
    }

    private fun refreshUserToken(jobAfterRefreshed: suspend () -> Unit) {
        viewModelScope.launch(getExceptionHandler {
            L.e(TAG, "RefreshToken failed!!!")
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
        refreshUserToken { loadUserInfoAndUpdate() }
    }

    private fun updateFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                task.result.let {
                    viewModelScope.launch {
                        userRepo.updateFcmToken(it)
                    }
                    L.i(TAG, "FCM Token Get!!! $it")
                }
            }
            else {
                L.e(TAG, "Retrieve FCM Token Failed!!! ${task.exception}")
            }
        }
    }

    fun clearUserInfoFromDB() {
        viewModelScope.launch {
            userRepo.clearUserInfoFromDB().also {
                if (it > 0) L.i(TAG, "UserInfo deleted!!!")
            }
        }
    }

    fun loadUserBonus() {
        L.i(TAG, "getUserBonus!!!")
        viewModelScope.launch {
            userRepo.getUserBonus()?.let {
                liveCurrentBalance.value = it.contentData
            }
        }
    }

    fun updateUserInfo(postBody: PostUpdateUserInfo, onFinish: (isSuccess: Boolean) -> Unit) {
        viewModelScope.launch(getExceptionHandler()) {
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

    fun deleteCreditCard(key: String) {
        viewModelScope.launch(getExceptionHandler()) {
            userRepo.deleteCreditCard(key)?.let {
                liveCreditCardDeleting.value = it
            }
        }
    }

    fun initPaymentDisplay() {
        val defaultCard = liveCreditCards.value?.cardData?.cardDetailList?.takeIf { it.isNotEmpty() }?.first()

        livePaymentDisplay.value = ItemPaymentDisplay(
            SettingManager.isCreditCardPaymentEnabled(),
            SettingManager.isPointPaymentEnabled(),
            defaultCard?.key,
            defaultCard?.name,
            defaultCard?.display?.substring(0, 4),
            defaultCard?.getBrandIconRes()
        )
    }

    fun updatePaymentMethod(isCreditCardEnabled: Boolean? = null, isPointEnabled: Boolean? = null) {
        val updatingValue = livePaymentDisplay.value

        updatingValue?.let {
            if (isCreditCardEnabled != null) {
                it.isCreditCardEnabled = isCreditCardEnabled
            }
            if (isPointEnabled != null) {
                it.isPointEnabled = isPointEnabled
            }
        }
        livePaymentDisplay.value = updatingValue

        loadPaymentCode()
    }

    fun updatePaymentCard(cardKey: String, cardName: String, cardNumber: String, brandIconRes: Int) {
        val updatingValue = livePaymentDisplay.value

        updatingValue?.let {
            it.selectedCardKey = cardKey
            it.cardName = cardName
            it.cardNumber = cardNumber.substring(0, 4)
            it.cardBrandRes = brandIconRes
        }
        livePaymentDisplay.value = updatingValue

        loadPaymentCode()
    }

    fun updatePaymentCoupon(couponName: String?, couponUuid: String?) {
        val updatingValue = livePaymentDisplay.value

        updatingValue?.let {
            it.selectedCouponName = couponName
            it.selectedCouponUuid = couponUuid
        }
        livePaymentDisplay.value = updatingValue

        loadPaymentCode()
    }

    fun loadPaymentCode() {
        viewModelScope.launch(getExceptionHandler()) {
            livePaymentDisplay.value?.let {
                val points = if (it.isPointEnabled) it.selectedPoints else 0
                val cardKey = if (it.isCreditCardEnabled) it.selectedCardKey else null

                userRepo.getPaymentCode(points, cardKey, it.selectedCouponUuid)?.let { payloadCodeItem ->
                    if (payloadCodeItem.isSuccess()) {
                        livePaymentDisplay.value?.paymentCode = payloadCodeItem.contentData.code.toString()
                        livePaymentDisplay.forceRefresh()
                    }
                }
            }
        }
    }
}