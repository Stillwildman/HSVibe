package com.hsvibe.callbacks

/**
 * Created by Vincent on 2021/06/28.
 */
interface OnLoadingCallback {

    fun onLoadingStart()

    fun onLoadingEnd()

    fun onLoadingFailed(errorMessage: String?)

}