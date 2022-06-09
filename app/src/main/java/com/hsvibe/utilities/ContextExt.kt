package com.hsvibe.utilities

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment

/**
 * Created by Vincent on 2021/6/29.
 */
fun Context?.isActivityAlive(): Boolean {
    return getActivity()?.isActivityAlive() ?: false
}

fun Context?.getActivity(): Activity? {
    var baseContext = this
    while (true) {
        when (baseContext) {
            is Activity -> return baseContext
            is ContextWrapper -> baseContext = baseContext.baseContext
            else -> return null
        }
    }
}

fun Activity?.isActivityAlive(): Boolean {
    return this != null
            && this.isFinishing.not()
            && this.isDestroyed.not()
}

fun Context?.isContextAlive(): Boolean {
    return getActivity()?.isActivityAlive() ?: (this != null)
}

@JvmOverloads
fun Context?.startActivitySafely(intent: Intent, exceptionHandler: (throwable: Throwable) -> Unit = { throwable ->
    throwable.printStackTrace()
}): Boolean {
    if (this == null) return false
    val couldStartSafely = when {
        isActivityAlive() -> {
            true
        }
        this !is Activity -> {
            intent.flags = intent.flags or Intent.FLAG_ACTIVITY_NEW_TASK
            true
        }
        else -> false
    }
    return if (couldStartSafely) {
        try {
            startActivity(intent)
            true
        } catch (throwable: Throwable) {
            exceptionHandler(throwable)
            false
        }
    } else false
}

fun Context?.startActivitySafelyAndFinish(intent: Intent, exceptionHandler: (throwable: Throwable) -> Unit = { throwable ->
    throwable.printStackTrace()
}) {
    startActivitySafely(intent, exceptionHandler)
    getActivity()?.finish()
}

fun Context?.launchBrowser(url: String): Boolean {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(if (url.startsWith("http")) url else "http://$url")
    return startActivitySafely(intent)
}

fun Context.shareText(content: String, chooserTitle: String) {
    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_TEXT, content)

    startActivity(Intent.createChooser(intent, chooserTitle))
}

fun Fragment.shareText(content: String, chooserTitle: String) {
    getContextSafely().shareText(content, chooserTitle)
}

fun Activity.shareText(content: String, chooserTitle: String) {
    (this as Context).shareText(content, chooserTitle)
}