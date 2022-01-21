package com.austin.stranded

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.austin.stranded.workers.PowerOnNotificationWorker

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