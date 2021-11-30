package com.example.stranded.workers

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.stranded.Repository
import com.example.stranded.sendNotification

class PowerOnNotificationWorker (private val context: Context, workerParams: WorkerParameters):
    Worker(context, workerParams) {

    override fun doWork(): Result {

// firing the notification
        val notificationManager =
            ContextCompat.getSystemService(
                context,
                NotificationManager::class.java
            ) as NotificationManager

        notificationManager.sendNotification(context, "--- INTERNAL POWER RECHARGED ---")

        val repository = Repository(context)

        val oldUserSave = repository.getUserSaveBlocking() // setting isPowered to true
        val newUserSave = oldUserSave.apply { isPowered = true }
        repository.noSuspendUpdateUserSaveData(newUserSave)

        val intent = Intent(context, PowerOnNotificationWorker::class.java)
        val oldPendingIntent = PendingIntent.getBroadcast(context, 1, intent,
            PendingIntent.FLAG_IMMUTABLE)

        oldPendingIntent.cancel() // cancelling the old pending intent

        return Result.success()
    }
}