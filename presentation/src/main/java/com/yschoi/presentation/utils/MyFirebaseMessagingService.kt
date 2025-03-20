package com.yschoi.presentation.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import com.yschoi.domain.model.DomainArtwork
import com.yschoi.domain.model.DomainUser
import com.yschoi.domain.usecase.authUseCase.SaveMessagingNewToken
import com.yschoi.presentation.MainActivity
import com.yschoi.presentation.R
import com.yschoi.presentation.view.ChatRoomActivity
import com.google.firebase.messaging.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.yschoi.domain.model.DomainChatRoomWithUser
import com.yschoi.presentation.view.chatRoom
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyFirebaseMessagingService: FirebaseMessagingService() {

    @Inject
    lateinit var sendTokenUseCase: SaveMessagingNewToken

    override fun handleIntent(intent: Intent) {
        val new = intent.apply {
            val temp = extras?.apply {
                remove(Constants.MessageNotificationKeys.ENABLE_NOTIFICATION)
                remove("gcm.notification.e")
            }
            replaceExtras(temp)
        }
        super.handleIntent(new)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        // FCM 메시지를 수신했을 때 호출됩니다.
        // 알림 메시지를 처리합니다.
        val data: Map<String, String> = p0.data
        sendNotification(title = data["title"]!!, body = data["body"]!!, data = data)
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        sendTokenUseCase.execute(token = p0)
    }

    private fun sendNotification(title: String, body: String, data: Map<String, String>) {
        val mainIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val chatIntent = Intent(applicationContext, ChatRoomActivity::class.java)
            .putExtra("artwork", data["artwork"])
            .putExtra("destUser", data["destUser"])
            .putExtra("chatRoom", data["chatRoomWithUser"])

        val stackBuilder = TaskStackBuilder.create(this).apply {
            addNextIntent(mainIntent)
            addNextIntent(chatIntent)
        }

        val requestCode = 0
        val pendingIntent = stackBuilder.getPendingIntent(
            requestCode,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
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

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

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