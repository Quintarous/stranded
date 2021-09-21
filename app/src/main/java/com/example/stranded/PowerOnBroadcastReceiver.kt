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

class PowerOnBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("bruh", "broadcast receiver triggered")

        //scheduling the work with work manager
        val workManager = WorkManager.getInstance(context!!)
        val workRequest = OneTimeWorkRequestBuilder<PowerOnNotificationWorker>()

        workManager.enqueue(workRequest.build())
    }
}