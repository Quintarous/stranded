package com.example.stranded

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.getSystemService
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.stranded.database.UserSave
import com.example.stranded.workers.PowerOnNotificationWorker
import javax.inject.Inject

/**
 * When the notification fires it triggers this broadcast receiver, which in turn enqueues the
 * worker that actually sends the notification to the users device (and updates the database).
 */
class PowerOnBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        // getting the work manager instance and work request
        val workManager = WorkManager.getInstance(context!!)
        val workRequest = OneTimeWorkRequestBuilder<PowerOnNotificationWorker>()

        // scheduling the work with work manager
        workManager.enqueue(workRequest.build())
    }
}