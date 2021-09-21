package com.example.stranded.workers

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.stranded.Repository
import com.example.stranded.database.UserSave
import com.example.stranded.sendNotification
import javax.inject.Inject

class PowerOnNotificationWorker (private val context: Context, workerParams: WorkerParameters):
    Worker(context, workerParams) {

    override fun doWork(): Result {
        //TODO change userSave.isPowered to true then fire a notification
        Log.i("bruh", "doWork called")
        val repository = Repository(context)

        //updating the database
        val oldUserSave = repository.userSave.value
//        val newUserSave = UserSave(1, true, oldUserSave!!.sequence, oldUserSave.line)
        val newUserSave = oldUserSave!!.apply { isPowered = true }

        repository.noSuspendUpdateUserSaveData(newUserSave)

        //firing the notification
        val notificationManager =
            context.getSystemService(NotificationManager::class.java) as NotificationManager
        //TODO the database is successfully changed but the notification never fires
        notificationManager.sendNotification(context, "--- INTERNAL POWER RECHARGED ---")

        return Result.success()
    }
}