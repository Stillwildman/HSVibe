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

    private const val PREF_USER_TOKEN = "UserToken"

    private const val PREF_ALREADY_ASKED_FOR_FULL_PROFILE = "AlreadyAskedForFullProfile"

    private const val PREF_NEWEST_NOTIFICATION_TIME = "NewestNotificationTime"

    private const val PREF_HAS_ENABLED_BIOMETRICS_VERIFYING = "HasEnabledBiometricsVerifying"

    private const val PREF_DEFAULT_CREDIT_CARD_KEY = "DefaultCreditCardKey"

    private const val PREF_PAYMENT_ENABLE_CREDIT_CARD = "EnableCreditCard"

    private const val PREF_PAYMENT_ENABLE_POINT = "EnablePoint"

    private fun appContext(): Context = AppController.getAppContext()

    private fun getDefaultPrefs(): SharedPreferences {
        return appContext().getSharedPreferences(FILE_DEFAULT_PREFS, Context.MODE_PRIVATE)
    }

    private fun getTokenPrefs(): SharedPreferences {
        return appContext().getSharedPreferences(FILE_TOKEN_PREFS, Context.MODE_PRIVATE)
    }

    fun setUserToken(userToken: UserToken) {
        getTokenPrefs().edit().putString(PREF_USER_TOKEN, Gson().toJson(userToken)).also { L.d("Saved UserToken:\n$it") }.apply()
    }

    fun clearUserToken() {
        getTokenPrefs().edit().remove(PREF_USER_TOKEN).apply()
    }

    @Synchronized
    fun getUserToken(): UserToken? {
        return getTokenPrefs().getString(PREF_USER_TOKEN, null)?.let { Gson().fromJson(it, UserToken::class.java) }
    }

    fun setFullProfileIsAlreadyAsked(isAlreadyAsked: Boolean) {
        getDefaultPrefs().edit().putBoolean(PREF_ALREADY_ASKED_FOR_FULL_PROFILE, isAlreadyAsked).apply()
    }
    
    fun isNeedToAskForFullProfile(): Boolean {
        return getDefaultPrefs().getBoolean(PREF_ALREADY_ASKED_FOR_FULL_PROFILE, false).not()
    }

    fun setNewestNotificationTime(time: Long) {
        getDefaultPrefs().edit().putLong(PREF_NEWEST_NOTIFICATION_TIME, time).apply()
    }

    fun getNewestNotificationTime(): Long {
        return getDefaultPrefs().getLong(PREF_NEWEST_NOTIFICATION_TIME, 0L).also { L.d("LastReadTime - getNewestNotificationTime: $it") }
    }

    fun enableBiometricVerifying(isEnable: Boolean) {
        getDefaultPrefs().edit().putBoolean(PREF_HAS_ENABLED_BIOMETRICS_VERIFYING, isEnable).apply()
    }

    fun isBiometricVerifyingEnabled(): Boolean {
        return getDefaultPrefs().getBoolean(PREF_HAS_ENABLED_BIOMETRICS_VERIFYING, true)
    }

    fun setDefaultCreditCardKey(key: String): Boolean {
        return getDefaultPrefs().edit().putString(PREF_DEFAULT_CREDIT_CARD_KEY, key).commit()
    }

    fun getDefaultCreditCardKey(): String? {
        return getDefaultPrefs().getString(PREF_DEFAULT_CREDIT_CARD_KEY, null)
    }

    fun clearDefaultCreditCardKey(): Boolean {
        return getDefaultPrefs().edit().remove(PREF_DEFAULT_CREDIT_CARD_KEY).commit()
    }

    fun setCreditCardPaymentEnabled(isEnable: Boolean) {
        getDefaultPrefs().edit().putBoolean(PREF_PAYMENT_ENABLE_CREDIT_CARD, isEnable).apply()
    }

    fun isCreditCardPaymentEnabled(): Boolean {
        return getDefaultPrefs().getBoolean(PREF_PAYMENT_ENABLE_CREDIT_CARD, true)
    }

    fun setPointPaymentEnabled(isEnable: Boolean) {
        getDefaultPrefs().edit().putBoolean(PREF_PAYMENT_ENABLE_POINT, isEnable).apply()
    }

    fun isPointPaymentEnabled(): Boolean {
        return getDefaultPrefs().getBoolean(PREF_PAYMENT_ENABLE_POINT, true)
    }
}