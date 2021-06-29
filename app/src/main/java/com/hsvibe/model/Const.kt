package com.hsvibe.model

import android.app.AlarmManager

/**
 * Created by Vincent on 2021/6/27.
 */
object Const {

    const val DIALOG_FRAGMENT = "DialogFragment"
    const val LOADING_DIALOG_FRAGMENT = "LoadingDialogFragment"

    const val BACK_COMMON_DIALOG = "CommonDialog"
    const val BACK_LOGIN_DIALOG = "LoginDialog"

    const val EMPTY_STRING = ""

    const val TOKEN_EXPIRED_DURATION = AlarmManager.INTERVAL_DAY * 20
}