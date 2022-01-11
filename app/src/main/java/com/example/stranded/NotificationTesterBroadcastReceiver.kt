package com.example.stranded

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat

class NotificationTesterBroadcastReceiver : BroadcastReceiver() {

    /**
     * All this broadcast receiver does is
     */
    override fun onReceive(context: Context, intent: Intent?) {

        val notificationManager = ContextCompat.getSystemService(
            context,
            NotificationManager::class.java
        ) as NotificationManager

        notificationManager.sendNotification(context, "hi")
    }
}