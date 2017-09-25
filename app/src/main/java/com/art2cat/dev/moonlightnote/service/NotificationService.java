package com.art2cat.dev.moonlightnote.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.art2cat.dev.moonlightnote.controller.moonlight.MoonlightActivity;
import com.art2cat.dev.moonlightnote.R;
import com.art2cat.dev.moonlightnote.utils.LogUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Rorschach
 * on 2016/11/10 19:50.
 */

public class NotificationService extends FirebaseMessagingService {
    private static final String TAG = "NotificationService";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        LogUtils.getInstance(TAG)
                .setContent("From: " + remoteMessage.getFrom())
                .debug();

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            LogUtils.getInstance(TAG)
                    .setContent("Message data payload: " + remoteMessage.getData())
                    .debug();
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            LogUtils.getInstance(TAG)
                    .setContent("Message Notification Body: " + remoteMessage.getNotification().getBody())
                    .debug();
            sendNotification(remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MoonlightActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Notification")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
