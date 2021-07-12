package com.hsvibe.tasks

import android.util.ArrayMap
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

/**
 * Created by Vincent on 2021/7/5.
 */
class TaskController<T> {

    private val activeTasks: ArrayMap<Int, Deferred<T>?> by lazy { ArrayMap() }

    private var cachedJob: Job? = null

    suspend fun joinPreviousOrRun(key: Int, block: suspend () -> T): T {
        // 如果當前有正在執行的 activeTask ，直接返回
        activeTasks[key]?.let {
            return it.await()
        }

        // 否則建立一個新的 async
        return coroutineScope {
            val newTask = async {
                block()
            }

            newTask.invokeOnCompletion {
                activeTasks[key] = null
                @Suppress("DeferredResultUnused")
                activeTasks.remove(key)
            }

            activeTasks[key] = newTask
            newTask.await()
        }
    }

}