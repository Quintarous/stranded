package com.example.stranded

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat

private const val NOTIFICATION_ID = 0
private const val CHANNEL_ID = "main"

/**
 * Creates and sends a styled notification with the given messageBody.
 */
fun NotificationManager.sendNotification(context: Context, messageBody: String) {

    // this notification will simply open the app
    val intent =  Intent(context, MainActivity::class.java)

    // wrapping it in a pending intent
    val pendingIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    // building and styling the notification
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentText(messageBody)
        .setSmallIcon(R.drawable.stranded_icon_no_background)
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)

    notify(NOTIFICATION_ID, builder.build()) // and finally sending it
}


/**
 * Creates a notification channel and registers it with the NotificationManager.
 */
fun createChannel(context: Context, channelId: String, channelName: String) {

    // Create the channel to show notifications.
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
    notificationChannel.description = "The one and only notification channel"

    // getting the notification manager
    val notificationManager = context.getSystemService(
        NotificationManager::class.java
    )

    // registering our newly created notification channel
    notificationManager.createNotificationChannel(notificationChannel)
}