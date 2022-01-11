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


/**
 * This worker actually sends the notification and changes UserSave.isPowered to true.
 */
class PowerOnNotificationWorker (private val context: Context, workerParams: WorkerParameters):
    Worker(context, workerParams) {

    override fun doWork(): Result {

        // getting the notification manager
        val notificationManager =
            ContextCompat.getSystemService(
                context,
                NotificationManager::class.java
            ) as NotificationManager

        // sending the notification (sendNotification extension function is found in NotificationUtils)
        notificationManager.sendNotification(context, "--- INTERNAL POWER RECHARGED ---")


        /**
         * Updating the UserSave
         */
        val repository = Repository(context) // getting the repository

        val oldUserSave = repository.getUserSaveBlocking() // getting the old user save

        val newUserSave = oldUserSave.apply { isPowered = true } // changing isPowered to true

        repository.noSuspendUpdateUserSaveData(newUserSave) // pushing the change to the db


        /**
         * We need to retrieve the pending intent used to trigger this work and cancel it. This
         * signals the work has been completed.
         *
         * If we don't it will just stick around forever and mess with the logic the next time around.
         */
        val intent = Intent(context, PowerOnNotificationWorker::class.java)

        // getting the old pending intent
        val oldPendingIntent = PendingIntent.getBroadcast(context, 1, intent,
            PendingIntent.FLAG_IMMUTABLE)

        oldPendingIntent.cancel() // cancelling the old pending intent

        return Result.success()
    }
}