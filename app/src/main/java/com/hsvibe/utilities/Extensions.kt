package com.hsvibe.utilities

import android.content.Context
import android.view.View
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.hsvibe.AppController
import com.hsvibe.callbacks.SingleClickListener
import com.hsvibe.ui.adapters.MyBaseAdapter

/**
 * Created by Vincent on 2021/8/16.
 */
object Extensions {

    fun View.setOnSingleClickListener(onClick: (v: View) -> Unit) {
        setOnClickListener(object : SingleClickListener() {
            override fun onSingleClick(v: View) {
                onClick(v)
            }
        })
    }

    fun View.setOnSingleClickListener(onSingleClickListener: SingleClickListener) {
        setOnClickListener(onSingleClickListener)
    }

    fun Spinner.getPairFirstValue(): String {
        return (adapter as? MyBaseAdapter)?.getFirst(selectedItemPosition) ?: ""
    }

    fun Spinner.getPairSecondValue(): String {
        return (adapter as? MyBaseAdapter)?.getSecond(selectedItemPosition) ?: ""
    }

    fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, onChanged: (item: T) -> Unit) {
        observe(lifecycleOwner) {
            onChanged(it)
            removeObservers(lifecycleOwner)
        }
    }

    fun Fragment.getContextSafely(): Context {
        return context ?: activity ?: AppController.getAppContext()
    }

    fun String?.isNotNullOrEmpty(): Boolean {
        return !this.isNullOrEmpty() && this != "null"
    }
}