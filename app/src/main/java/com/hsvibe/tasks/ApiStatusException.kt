package com.hsvibe.tasks

import com.hsvibe.model.ApiStatus
import com.hsvibe.utilities.L

/**
 * Created by Vincent on 2021/7/6.
 */
class ApiStatusException(val statusCode: Int, val errorMessage: String?) : Exception() {

    init {
        L.e("StatusCode: $statusCode ErrorMessage: $errorMessage")
        printStackTrace()
    }

    fun isTokenStatusExpired(): Boolean = statusCode == ApiStatus.STATUS_TOKEN_EXPIRED
}