package com.hsvibe.repositories

import com.hsvibe.callbacks.OnLoadingCallback
import com.hsvibe.model.UserTokenManager
import com.hsvibe.model.items.ItemMessage
import com.hsvibe.model.posts.PostPassword
import com.hsvibe.network.DataCallbacks

/**
 * Created by Vincent on 2022/5/9.
 */
class PayPasswordRepoImpl : PayPasswordRepo {

    private var callback: OnLoadingCallback? = null

    override fun setLoadingCallback(loadingCallback: OnLoadingCallback?) {
        this.callback = loadingCallback
    }

    override suspend fun verifyPayPassword(code: String): ItemMessage? {
        return UserTokenManager.getAuthorization()?.let {
            DataCallbacks.verifyPayPassword(it, PostPassword(code), callback)
        }
    }
}