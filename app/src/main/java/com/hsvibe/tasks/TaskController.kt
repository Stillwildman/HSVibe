package com.hsvibe.tasks

import android.util.ArrayMap
import com.hsvibe.utilities.L
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * Created by Vincent on 2021/7/5.
 */
class TaskController<T> {

    companion object {
        private const val TAG = "TaskController"

        const val KEY_GET_USER_INFO = 0
        const val KEY_UPDATE_USER_INFO = 1
        const val KEY_GET_AND_UPDATE_USER_INFO = 2
        const val KEY_SWITCH_TAB_FRAGMENT = 3
        const val KEY_OPEN_FRAGMENT = 4
        const val KEY_OPEN_DIALOG_FRAGMENT = 5
    }

    private val activeTasks: ArrayMap<Int, Deferred<T>?> by lazy { ArrayMap() }

    suspend fun joinPreviousOrRun(key: Int, block: suspend () -> T): T {
        // 如果當前有正在執行的 activeTask ，直接返回
        activeTasks[key]?.let {
            L.d(TAG, "Has ActiveTask!!! Key: $key")
            return it.await()
        }

        // 否則建立一個新的 async
        return coroutineScope {
            val newTask = async {
                block()
            }

            newTask.invokeOnCompletion {
                L.d(TAG, "Nes Task Completed!!! Key: $key")
                activeTasks[key] = null
                @Suppress("DeferredResultUnused")
                activeTasks.remove(key)
            }

            activeTasks[key] = newTask
            newTask.await()
        }
    }

}