package com.art2cat.dev.moonlightnote.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import com.art2cat.dev.moonlightnote.R
import com.art2cat.dev.moonlightnote.controller.moonlight.MoonlightActivity
import com.art2cat.dev.moonlightnote.utils.LogUtils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Created by Rorschach
 * on 20/05/2017 11:33 PM.
 */

class NotificationService : FirebaseMessagingService() {

    /**
     * Called when message is received.

     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        LogUtils.getInstance(TAG)
                .setMessage("From: " + remoteMessage!!.from)
                .debug()

        // Check if message contains a data payload.
        if (remoteMessage.data.isNotEmpty()) {
            LogUtils.getInstance(TAG)
                    .setMessage("Message data payload: " + remoteMessage.data)
                    .debug()
        }

        // Check if message contains a notification payload.
        if (remoteMessage.notification != null) {
            LogUtils.getInstance(TAG)
                    .setMessage("Message Notification Body: " + remoteMessage.notification.body!!)
                    .debug()
            sendNotification(remoteMessage.notification.body as String)
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.

     * @param messageBody FCM message body received.
     */
    private fun sendNotification(messageBody: String) {
        val intent = Intent(this, MoonlightActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT)

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Notification")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }

    companion object {
        private val TAG = "NotificationService"
    }
}
