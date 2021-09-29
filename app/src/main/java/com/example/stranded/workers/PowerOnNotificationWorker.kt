package com.example.stranded.workers

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.stranded.Repository
import com.example.stranded.database.UserSave
import com.example.stranded.sendNotification
import javax.inject.Inject

class PowerOnNotificationWorker (private val context: Context, workerParams: WorkerParameters):
    Worker(context, workerParams) {

    override fun doWork(): Result {
        Log.i("bruh", "doWork called")
        val repository = Repository(context)

        //updating the database
        // TODO test that this user save database change has the intended effect with a real (not in memory) database
        val oldUserSave = repository.getUserSave()
        val newUserSave = oldUserSave.apply { isPowered = true }
        repository.noSuspendUpdateUserSaveData(newUserSave)

        //firing the notification
        val notificationManager =
            ContextCompat.getSystemService(
                context,
                NotificationManager::class.java
            ) as NotificationManager

        notificationManager.sendNotification(context, "--- INTERNAL POWER RECHARGED ---")
        Log.i("bruh", "notification fired")

        return Result.success()
    }
}