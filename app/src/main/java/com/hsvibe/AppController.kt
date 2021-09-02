package com.hsvibe

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication

/**
 * Created by Vincent on 2021/6/25.
 */
class AppController : MultiDexApplication() {

    companion object {
        @get:Synchronized
        lateinit var instance: AppController

        fun getAppContext(): Context = instance.applicationContext

        fun getString(@StringRes stringRes: Int): String = instance.applicationContext.getString(stringRes)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun hideKeyboard(view: View) {
        val imm = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.applicationWindowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

}