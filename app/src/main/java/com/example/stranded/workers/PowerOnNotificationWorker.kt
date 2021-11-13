package com.example.stranded.workers

import android.app.NotificationManager
import android.content.Context
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

// setting isPowered to true
// TODO test that this user save database change has the intended effect with a real (not in memory) database
        val oldUserSave = repository.getUserSaveBlocking()
        val newUserSave = oldUserSave.apply { isPowered = true }
        repository.noSuspendUpdateUserSaveData(newUserSave)


        return Result.success()
    }
}