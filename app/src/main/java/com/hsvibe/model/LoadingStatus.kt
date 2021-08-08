package com.hsvibe.model

/**
 * Created by Vincent on 2021/8/5.
 */
sealed class LoadingStatus {

    object OnLoadingStart : LoadingStatus()

    object OnLoadingEnd : LoadingStatus()

    class OnError(val errorMessage: String) : LoadingStatus()
}
