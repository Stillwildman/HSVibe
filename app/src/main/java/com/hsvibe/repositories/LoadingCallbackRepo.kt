package com.hsvibe.repositories

import com.hsvibe.callbacks.OnLoadingCallback

/**
 * Created by Vincent on 2022/5/9.
 */
interface LoadingCallbackRepo {

    fun setLoadingCallback(loadingCallback: OnLoadingCallback?)

}