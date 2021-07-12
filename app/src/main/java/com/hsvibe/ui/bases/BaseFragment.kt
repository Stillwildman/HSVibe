package com.hsvibe.ui.bases

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.hsvibe.R
import com.hsvibe.callbacks.FragmentContract
import com.hsvibe.utilities.L
import kotlinx.coroutines.Job
import kotlinx.coroutines.async

/**
 * Created by Vincent on 2021/7/4.
 */
abstract class BaseFragment<BindingView : ViewDataBinding> : Fragment(), FragmentContract.ActivityCallback {

    @Suppress("PropertyName")
    protected val TAG: String = javaClass.simpleName

    protected abstract fun getLayoutId(): Int
    protected abstract fun init()

    protected lateinit var bindingView: BindingView

    private var fragmentCallback: FragmentContract.FragmentCallback? = null

    private lateinit var lifecycleJob: Job

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
        return inflater.inflate(R.layout.fragment_empty_container, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        L.d(TAG, "onViewCreated!!!")

        lifecycleJob = lifecycleScope.launchWhenCreated {
            val bindingViewDeferred = async {
                DataBindingUtil.inflate(LayoutInflater.from(view.context), getLayoutId(), view as ViewGroup?, false) as BindingView
            }
            bindingView = bindingViewDeferred.await()

            (view as ViewGroup?)?.addView(bindingView.root)

            init()
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