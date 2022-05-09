package com.hsvibe.repositories

import com.hsvibe.model.items.ItemMessage

/**
 * Created by Vincent on 2022/5/9.
 */
interface PayPasswordRepo : LoadingCallbackRepo {

    suspend fun verifyPayPassword(code: String): ItemMessage?

}