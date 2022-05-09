package com.hsvibe.tasks

import com.hsvibe.model.ApiStatus
import com.hsvibe.model.items.ItemMessage
import com.hsvibe.utilities.L

/**
 * Created by Vincent on 2021/7/6.
 */
class ApiStatusException(val statusCode: Int, val errorBody: String?, val messageItem: ItemMessage?) : Exception() {

    init {
        L.e("StatusCode: $statusCode ErrorBody: $errorBody")
        printStackTrace()
    }

    fun isTokenStatusExpired(): Boolean = statusCode == ApiStatus.STATUS_TOKEN_EXPIRED

    fun isDataVerifyFailed(): Boolean = statusCode == ApiStatus.STATUS_DATA_VERIFY_FAILED

    fun isServerError(): Boolean = statusCode >= 500
}