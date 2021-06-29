package com.hsvibe.utilities

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.hsvibe.AppController
import com.hsvibe.model.UserToken

/**
 * Created by Vincent on 2021/6/28.
 */
object SettingManager {

    private const val FILE_DEFAULT_PREFS = "General.pref"

    private const val FILE_TOKEN_PREFS = "UserToken.pref"

    private const val PREF_USER_TOKEN = "UserTokenPref"

    private fun appContext(): Context = AppController.getAppContext()

    private fun getDefaultPrefs(): SharedPreferences {
        return appContext().getSharedPreferences(FILE_DEFAULT_PREFS, Context.MODE_PRIVATE)
    }

    private fun getTokenPrefs(): SharedPreferences {
        return appContext().getSharedPreferences(FILE_TOKEN_PREFS, Context.MODE_PRIVATE)
    }

    fun setUserToken(userToken: UserToken) {
        getTokenPrefs().edit().putString(PREF_USER_TOKEN, Gson().toJson(userToken)).apply()
    }

    fun getUserToken(): UserToken? {
        return getTokenPrefs().getString(PREF_USER_TOKEN, null)?.let { Gson().fromJson(it, UserToken::class.java) }
    }
}