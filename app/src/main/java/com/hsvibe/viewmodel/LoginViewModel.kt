package com.hsvibe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hsvibe.model.UserToken

/**
 * Created by Vincent on 2021/6/28.
 */
class LoginViewModel : ViewModel() {

    val liveUserToken: MutableLiveData<UserToken> = MutableLiveData()

    fun updateUserToken(userToken: UserToken) {
        liveUserToken.value = userToken
    }

    fun refreshUserToken() {

    }
}