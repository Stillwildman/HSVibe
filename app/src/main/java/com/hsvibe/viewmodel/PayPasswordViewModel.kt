package com.hsvibe.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hsvibe.R
import com.hsvibe.model.items.ItemMessage
import com.hsvibe.repositories.PayPasswordRepo
import com.hsvibe.tasks.ApiStatusException
import com.hsvibe.utilities.L
import com.hsvibe.utilities.Utility
import com.hsvibe.widgets.CodeInputLayout
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

/**
 * Created by Vincent on 2022/5/9.
 */
class PayPasswordViewModel(private val payPasswordRepo: PayPasswordRepo) : LoadingStatusViewModel() {

    init {
        payPasswordRepo.setLoadingCallback(this)
    }

    val liveShowConfirmationInput: LiveData<Boolean>
        get() = _showConfirmationInput

    private val _showConfirmationInput by lazy { MutableLiveData<Boolean>() }

    val liveEnableButton: LiveData<Boolean>
        get() = _enableButton

    private val _enableButton by lazy { MutableLiveData<Boolean>() }

    val liveState: LiveData<Int>
        get() = _state

    private val _state by lazy { MutableLiveData(CodeInputLayout.STATE_NORMAL) }

    val liveMessage: LiveData<ItemMessage>
        get() = _message

    private val _message by lazy { MutableLiveData<ItemMessage>() }

    override fun getExceptionHandler(): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, throwable ->
            L.e("Handle Coroutine Exception!!!")
            when {
                !Utility.isNetworkEnabled() -> {
                    L.e("Network is not working!!!")
                    Utility.toastLong("Network is not working!!!")
                }
                throwable is ApiStatusException && throwable.isDataVerifyFailed() -> {
                    _message.value = throwable.messageItem
                }
                throwable is ApiStatusException -> {
                    Utility.toastLong("Api Error!!\nCode: ${throwable.statusCode}\nMsg: ${throwable.errorBody}")
                }
                else -> {
                    Utility.toastLong(R.string.unknown_network_error)
                }
            }
            onLoadingEnd()
            throwable.printStackTrace()
        }
    }

    fun showConfirmationInput(isShow: Boolean) {
        _showConfirmationInput.value = isShow
    }

    fun enableButton(isEnable: Boolean) {
        _enableButton.value = isEnable
    }

    fun setInputLayoutState(state: Int) {
        _state.value = state
    }

    fun verifyPayPassword(code: String) {
        viewModelScope.launch(getExceptionHandler()) {
            payPasswordRepo.verifyPayPassword(code)?.let {
                _message.value = it
            }
        }
    }

}