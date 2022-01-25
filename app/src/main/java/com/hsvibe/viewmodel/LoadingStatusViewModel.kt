package com.hsvibe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hsvibe.R
import com.hsvibe.callbacks.OnLoadingCallback
import com.hsvibe.model.LoadingStatus
import com.hsvibe.tasks.ApiStatusException
import com.hsvibe.utilities.L
import com.hsvibe.utilities.Utility
import kotlinx.coroutines.CoroutineExceptionHandler

/**
 * Created by Vincent on 2021/8/5.
 */
open class LoadingStatusViewModel : ViewModel(), OnLoadingCallback {

    val liveLoadingStatus = MutableLiveData<LoadingStatus>()

    override fun onLoadingStart() {
        liveLoadingStatus.postValue(LoadingStatus.OnLoadingStart)
    }

    override fun onLoadingEnd() {
        liveLoadingStatus.postValue(LoadingStatus.OnLoadingEnd)
    }

    override fun onLoadingFailed(errorMessage: String?) {
        liveLoadingStatus.postValue(LoadingStatus.OnError(errorMessage ?: ""))
    }

    open fun getExceptionHandler(): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, throwable ->
            L.e("Handle Coroutine Exception!!!")
            when {
                throwable is ApiStatusException -> {
                    Utility.toastLong("Api Error!!\nCode: ${throwable.statusCode}\nMsg: ${throwable.errorMessage}")
                }
                !Utility.isNetworkEnabled() -> {
                    L.e("Network is not working!!!")
                    Utility.toastLong("Network is not working!!!")
                }
                else -> {
                    Utility.toastLong(R.string.unknown_network_error)
                }
            }
            onLoadingEnd()
            throwable.printStackTrace()
        }
    }
}