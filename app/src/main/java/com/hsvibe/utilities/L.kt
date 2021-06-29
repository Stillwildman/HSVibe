package com.hsvibe.utilities

import android.util.Log
import com.hsvibe.BuildConfig

/**
 * Created by Vincent on 2021/6/27.
 */
object L {

    fun d(msg: String) {
        if (BuildConfig.DEBUG) {
            d(null, msg)
        }
    }

    fun d(tag: String?, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag ?: getTag(), msg)
        }
    }

    fun i(msg: String) {
        if (BuildConfig.DEBUG) {
            i(null, msg)
        }
    }

    fun i(tag: String?, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.i(tag ?: getTag(), msg)
        }
    }

    fun e(msg: String) {
        if (BuildConfig.DEBUG) {
            e(null, msg)
        }
    }

    fun e(tag: String?, msg: String) {
        if (BuildConfig.DEBUG) {
            Log.e(tag ?: getTag(), msg)
        }
    }

    private fun getTag(): String {
        val callers = Thread.currentThread().stackTrace

        val caller = if (callers.size > 6) callers[5] else callers[callers.size - 1]

        val className = caller.className.let { it.substring(it.lastIndexOf(".") + 1) }

        return StringBuilder().run {
            append(className)
            append(".")
            append(caller.methodName)
            append("():")
            append(caller.lineNumber)
            toString()
        }
    }
}