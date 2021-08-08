package com.hsvibe.ui.bases

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.hsvibe.R
import com.hsvibe.model.LoadingStatus
import com.hsvibe.utilities.L
import com.hsvibe.utilities.Utility
import kotlinx.coroutines.*

/**
 * Created by Vincent on 2021/6/28.
 */
abstract class BaseActivity<BindingView : ViewDataBinding> : BaseFragmentActivity() {

    @Suppress("PropertyName")
    protected val TAG = javaClass.simpleName

    protected abstract fun getLayoutId(): Int

    protected abstract fun getLoadingView(): View?

    protected abstract fun init()

    protected lateinit var bindingView: BindingView

    private var exitTime : Long = 0

    protected val mainHandler by lazy { Handler(mainLooper) }

    private lateinit var job: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        job = launch {
            val deferredBindingView = async {
                DataBindingUtil.setContentView(this@BaseActivity, getLayoutId()) as BindingView
            }
            bindingView = deferredBindingView.await()
            init()
        }

        L.d(TAG, "onCreate!!!")
    }

    protected fun showLoadingCircle() {
        getLoadingView()?.visibility = View.VISIBLE
    }

    protected fun hideLoadingCircle() {
        getLoadingView()?.visibility = View.GONE
    }

    protected fun handleLoadingStatus(loadingStatus: LoadingStatus) {
        when (loadingStatus) {
            is LoadingStatus.OnLoadingStart -> showLoadingCircle()
            is LoadingStatus.OnLoadingEnd -> hideLoadingCircle()
            is LoadingStatus.OnError -> Utility.toastLong(loadingStatus.errorMessage)
        }
    }

    override fun onStart() {
        super.onStart()
        L.d(TAG, "onStart!!!")
    }

    override fun onResume() {
        super.onResume()
        L.d(TAG, "onResume!!!")
    }

    override fun onPause() {
        super.onPause()
        L.d(TAG, "onPause!!!")
    }

    override fun onStop() {
        super.onStop()
        L.d(TAG, "onStop!!!")
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        L.d(TAG, "onDestroy!!!")
    }

    override fun onBackPressed() {
        L.d(TAG, "onBackPressed!!!")

        if (isFragmentsMoreThanOne()) {
            super.onBackPressed()
        }
        else {
            finishAppWithinDoubleTap()
        }
    }

    private fun finishAppWithinDoubleTap() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            Utility.toastShort(R.string.confirm_to_exit)
            exitTime = System.currentTimeMillis()
        }
        else {
            Utility.forceCloseTask()
        }
    }

    override fun onPermissionGranted(requestCode: Int) {

    }

    override fun onPermissionDenied(requestCode: Int) {

    }
}