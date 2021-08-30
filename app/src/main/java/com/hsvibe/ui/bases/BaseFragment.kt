package com.hsvibe.ui.bases

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.hsvibe.R
import com.hsvibe.callbacks.FragmentContract
import com.hsvibe.model.LoadingStatus
import com.hsvibe.utilities.L
import com.hsvibe.utilities.Utility
import kotlinx.coroutines.async

/**
 * Created by Vincent on 2021/7/4.
 */
abstract class  BaseFragment<BindingView : ViewDataBinding> : Fragment(R.layout.fragment_empty_container), FragmentContract.ActivityCallback {

    @Suppress("PropertyName")
    protected val TAG: String = javaClass.simpleName

    protected abstract fun getLayoutId(): Int
    protected abstract fun getLoadingView(): View?
    protected abstract fun init()

    protected lateinit var bindingView: BindingView

    private var fragmentCallback: FragmentContract.FragmentCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            fragmentCallback = context as FragmentContract.FragmentCallback
            fragmentCallback?.setupActivityCallback(this)
        }
        catch (e: ClassCastException) {
            e.printStackTrace()
            L.e(TAG, context.javaClass.simpleName + " must implement " + FragmentContract.FragmentCallback::class.java.simpleName)
        }
        L.d(TAG, "onAttach!!!")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        L.d(TAG, "onCreate!!!")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        L.d(TAG, "onCreateView!!!")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        L.d(TAG, "onViewCreated!!!")

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            val bindingViewDeferred = async {
                DataBindingUtil.inflate(LayoutInflater.from(view.context), getLayoutId(), view as ViewGroup?, false) as BindingView
            }
            bindingView = bindingViewDeferred.await()

            (view as ViewGroup?)?.addView(bindingView.root)

            init()
        }
    }

    protected fun openDialogFragment(instance: DialogFragment, backName: String? = null) {
        fragmentCallback?.onFragmentOpenDialogFragment(instance, backName)
    }

    protected fun openWebDialogFragment(url: String) {
        fragmentCallback?.onFragmentOpenWebDialogFragment(url)
    }

    protected fun checkPermissionThenOpenDialogFragment(permissionRequestCode: Int, instance: DialogFragment) {
        fragmentCallback?.checkPermissionThenOpenDialogFragment(permissionRequestCode, instance)
    }

    protected open fun showLoading() {
        getLoadingView()?.visibility = View.VISIBLE
    }

    protected open fun hideLoading() {
        getLoadingView()?.visibility = View.GONE
    }

    protected open fun handleLoadingStatus(loadingStatus: LoadingStatus) {
        when (loadingStatus) {
            is LoadingStatus.OnLoadingStart -> showLoading()
            is LoadingStatus.OnLoadingEnd -> hideLoading()
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

    override fun onDestroyView() {
        super.onDestroyView()
        L.d(TAG, "onDestroyView!!!")
    }

    override fun onDestroy() {
        super.onDestroy()
        L.d(TAG, "onDestroy!!!")
    }

    override fun onDetach() {
        super.onDetach()
        fragmentCallback?.setupActivityCallback(null)
    }
}