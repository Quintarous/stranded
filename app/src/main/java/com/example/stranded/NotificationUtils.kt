package com.example.stranded

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat

private val NOTIFICATION_ID = 0
private val CHANNEL_ID = "test"

//creates and publishes a default looking notification with the given message body
fun NotificationManager.sendNotification(context: Context, messageBody: String) {
    val intent =  Intent(context, MainActivity::class.java)

    val pendingIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentText(messageBody)
        .setSmallIcon(R.drawable.ic_launcher_foreground)
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    notify(NOTIFICATION_ID, builder.build())
}

//creates a notification channel and registers it with the NotificationManager
fun createChannel(context: Context, channelId: String, channelName: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Create channel to show notifications.
        val notificationChannel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_HIGH
        )
            .apply {
                setShowBadge(false)
            }

        notificationChannel.enableLights(true)
        notificationChannel.lightColor = Color.GREEN
        notificationChannel.enableVibration(true)
        notificationChannel.description = "Test Channel"

        val notificationManager = context.getSystemService(
            NotificationManager::class.java
        )

        notificationManager.createNotificationChannel(notificationChannel)
    }
}