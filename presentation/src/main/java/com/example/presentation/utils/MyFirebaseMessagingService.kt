package com.example.presentation.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.presentation.R
import com.example.presentation.view.ChatRoomActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService: FirebaseMessagingService() {
    override fun getStartCommandIntent(p0: Intent): Intent {
        return super.getStartCommandIntent(p0)
    }

    override fun handleIntent(intent: Intent) {
        super.handleIntent(intent)
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        // FCM 메시지를 수신했을 때 호출됩니다.
        if (p0.getNotification() != null) {
            // 알림 메시지를 처리합니다.
            val title: String = p0.notification!!.title.toString()
            val body: String = p0.notification!!.body.toString()
            Log.d("onMessageReceived", "title: ${title}, body: ${body}, all: ${p0.notification}")
            sendNotification(title = title, body = body)
        }
    }

    override fun onMessageSent(p0: String) {
        super.onMessageSent(p0)
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    override fun onSendError(p0: String, p1: Exception) {
        super.onSendError(p0, p1)
    }


    private fun sendNotification(title: String, body: String) {
        val intent = Intent(this, ChatRoomActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val requestCode = 0
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        val channelId = "fcm_default_channel"
        val groupId = "chatting"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.app_logo)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setGroup(groupId)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = 0
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}