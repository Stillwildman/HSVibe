package com.hsvibe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.hsvibe.model.UserToken
import com.hsvibe.utilities.L

/**
 * Created by Vincent on 2021/6/28.
 */
class LoginViewModel : ViewModel() {

    val liveUserToken: MutableLiveData<UserToken> = MutableLiveData()

    fun updateUserToken(userToken: UserToken) {
        liveUserToken.postValue(userToken)
    }

    fun updateUserTokenByRawData(jsonString: String) {
        Gson().fromJson(jsonString, UserToken::class.java).also {
            L.i("Get new UserToken:\n$it")
            liveUserToken.postValue(it)
        }
    }

    fun refreshUserToken() {

    }
}