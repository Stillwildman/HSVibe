package com.hsvibe.utilities

import android.content.Context
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.callbacks.SingleClickListener
import com.hsvibe.ui.adapters.MyBaseAdapter
import java.nio.charset.StandardCharsets

/**
 * Created by Vincent on 2021/8/16.
 */
fun View.setOnSingleClickListener(onClick: (v: View) -> Unit) {
    setOnClickListener(object : SingleClickListener() {
        override fun onSingleClick(v: View) {
            onClick(v)
        }
    })
}

fun View.setOnSingleClickListener(onSingleClickListener: SingleClickListener?) {
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

fun View.getInflatedSize(onMeasuredSizeGet: (width: Int, height: Int) -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            onMeasuredSizeGet(width, height)
        }
    })
}

fun SwipeRefreshLayout.init(onRefreshListener: SwipeRefreshLayout.OnRefreshListener) {
    setColorSchemeResources(
        R.color.md_green_500, R.color.md_amber_400,
        R.color.md_light_blue_A700, R.color.md_red_500
    )
    setOnRefreshListener(onRefreshListener)
}

fun String?.isNotNullOrEmpty(): Boolean {
    return !this.isNullOrEmpty() && this != "null"
}

fun String.getQRCodeText(): String {
    return String(this.toByteArray(), StandardCharsets.ISO_8859_1)
}

fun <T> MutableLiveData<T>.forceRefresh() {
    this.value = this.value
}

fun View.useFadingAnimation(duration: Long = 300, completion: (() -> Unit)? = null) {
    fadeOutAnimation(duration) {
        fadeInAnimation(duration) {
            completion?.let {
                it()
            }
        }
    }
}

fun View.fadeOutAnimation(duration: Long = 300, completion: (() -> Unit)? = null) {
    val alphaAnim = AlphaAnimation(1f, 0f).apply {
        this.duration = duration
        setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                visibility = View.GONE
                completion?.let {
                    it()
                }
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    this.startAnimation(alphaAnim)
}

fun View.fadeInAnimation(duration: Long = 300, completion: (() -> Unit)? = null) {
    val alphaAnim = AlphaAnimation(0f, 1f).apply {
        this.duration = duration
        setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
                visibility = View.VISIBLE
            }

            override fun onAnimationEnd(animation: Animation?) {
                completion?.let {
                    it()
                }
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    this.startAnimation(alphaAnim)
}