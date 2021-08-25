package com.hsvibe.fcm

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hsvibe.model.UserTokenManager
import com.hsvibe.model.posts.PostUpdateUserInfo
import com.hsvibe.network.DataCallbacks
import com.hsvibe.utilities.L
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by Vincent on 2021/8/24.
 */
class FcmService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // TODO Customize notification!
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        L.d("onNewTokenGet!!!\n$token")

        CoroutineScope(Dispatchers.IO).launch {
            UserTokenManager.getAuthorization()?.let {
                DataCallbacks.updateUserInfo(it, PostUpdateUserInfo(fcm_token = token))
                L.i("onNewToken Sent to server!!!")
            }
        }
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
        L.i("onDeletedMessages!!!")
    }
}