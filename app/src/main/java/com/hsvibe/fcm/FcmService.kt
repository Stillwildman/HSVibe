package com.hsvibe.fcm

import android.os.Bundle
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hsvibe.events.Events
import com.hsvibe.model.ApiConst
import com.hsvibe.model.Const
import com.hsvibe.model.UserTokenManager
import com.hsvibe.model.posts.PostUpdateUserInfo
import com.hsvibe.network.DataCallbacks
import com.hsvibe.utilities.L
import com.hsvibe.utilities.NotifyHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus

/**
 * Created by Vincent on 2021/8/24.
 */
class FcmService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        L.i("onMessageReceived!! from: ${remoteMessage.from} data size: ${remoteMessage.data.size}")
        L.i("data:")
        remoteMessage.data.keys.forEach {
            L.i("$it : ${remoteMessage.data[it]}")
        }

        val type = remoteMessage.data[ApiConst.TYPE]

        type?.toIntOrNull()?.let {
            when (it) {
                ApiConst.TYPE_ON_COUPON_USED -> {
                    sendOnCouponUsedEvent()
                }
                ApiConst.TYPE_ON_BONUS_GET -> {
                    sendOnBonusGetEvent(remoteMessage.data)
                }
                ApiConst.TYPE_ON_POINT_RECEIVED -> {
                    sendOnPointReceivedEvent()
                }
            }
        }
        remoteMessage.notification?.let { notification ->
            L.i("notification:")
            L.i("title : ${notification.title} body : ${notification.body}")

            when (type?.toIntOrNull()) {
                ApiConst.TYPE_ON_COUPON_REDEEM -> {
                    NotifyHelper.showCommonNotification(notification.title ?: "", notification.body ?: "", Const.ACTION_COUPON)
                }
                ApiConst.TYPE_ON_NEWS_GET -> {
                    showNewsNotification(remoteMessage)
                }
                else -> {
                    NotifyHelper.showCommonNotification(notification.title ?: "", notification.body ?: "")
                }
            }
        }
    }

    private fun showNewsNotification(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let {
            NotifyHelper.showCommonNotification(it.title ?: "", it.body ?: "", Const.ACTION_NEWS, Bundle().apply {
                putString(Const.BUNDLE_UUID, remoteMessage.data[ApiConst.ID])
            })
            L.i("showNewsNotification!!!")
        }
    }

    private fun sendOnCouponUsedEvent() {
        EventBus.getDefault().post(Events.OnCouponUsed())
    }

    private fun sendOnBonusGetEvent(data: Map<String, String>) {
        val amount = data[ApiConst.ACTUAL_AMOUNT]
        val rewardPoint = data[ApiConst.REWARD_POINT]

        if (amount != null && rewardPoint != null) {
            EventBus.getDefault().post(Events.OnBonusGet(amount, rewardPoint))
        }
    }

    private fun sendOnPointReceivedEvent() {
        EventBus.getDefault().post(Events.OnPointReceived())
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