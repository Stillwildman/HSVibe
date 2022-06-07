package com.hsvibe.utilities

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.hsvibe.AppController
import com.hsvibe.R
import com.hsvibe.ui.UiMainActivity

/**
 * Created by Vincent on 2022/6/6.
 */
object NotifyHelper {

    private const val NOTIFY_ID_COMMON = 1
    private const val CHANNEL_ID_TRANSACTION = "ChannelTransaction"

    fun showCommonNotification(title: String, body: String, pendingAction: String? = null, bundle: Bundle? = null) {
        val intent = Intent(AppController.getAppContext(), UiMainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
            pendingAction?.let { action = it }
            bundle?.let { putExtras(it) }
        }

        val pendingIntentFlag =  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getActivity(AppController.getAppContext(), 0, intent, pendingIntentFlag)

        val builder = NotificationCompat.Builder(AppController.getAppContext(), CHANNEL_ID_TRANSACTION)

        val bigTextStyle = NotificationCompat.BigTextStyle()
        bigTextStyle.setBigContentTitle(title)
        bigTextStyle.bigText(body)

        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        builder.setContentIntent(pendingIntent)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(body)
            .setTicker(body)
            .setStyle(bigTextStyle)
            .setWhen(System.currentTimeMillis())
            .setAutoCancel(true)
            .setSound(soundUri)
            .setDefaults(Notification.DEFAULT_ALL)
            .setFullScreenIntent(pendingIntent, false)

//        val drawable = ContextCompat.getDrawable(AppController.getAppContext(), R.mipmap.ic_launcher)
//
//        drawable?.let {
//            val iconSize = AppController.getAppContext().resources.getDimensionPixelSize(R.dimen.icon_common_size_l)
//            val appIcon = BitmapUtil.convertDrawableToBitmap(drawable, iconSize, iconSize)
//            builder.setLargeIcon(appIcon)
//        }

        NotificationManagerCompat.from(AppController.getAppContext()).notify(NOTIFY_ID_COMMON, builder.build())
    }

    fun createChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val nm = AppController.getAppContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val channel = NotificationChannel(CHANNEL_ID_TRANSACTION, AppController.getString(R.string.channel_title_transaction), NotificationManager.IMPORTANCE_HIGH)
            channel.description = AppController.getString(R.string.channel_description_transaction)
            channel.enableLights(true)
            channel.enableVibration(true)

            nm.createNotificationChannel(channel)
        }
    }
}