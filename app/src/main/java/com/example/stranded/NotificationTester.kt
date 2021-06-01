package com.example.stranded

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.SystemClock
import android.util.Log
import androidx.core.app.AlarmManagerCompat

class NotificationTester(private val context: Context) {

    private val REQUEST_CODE = 0

    private val notifyIntent = Intent(context, NotificationTesterBroadcastReceiver::class.java)
//    private val notifyPendingIntent = PendingIntent.getBroadcast(
//        context,
//        REQUEST_CODE,
//        notifyIntent,
//        PendingIntent.FLAG_UPDATE_CURRENT
//    )

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    //schedules a given number of test notifications
    fun scheduleNotifications(quantity: Int) {
        val calendars = generateCalendars(quantity)

        for (calendar in calendars) {
            AlarmManagerCompat.setExactAndAllowWhileIdle(
                alarmManager,
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                PendingIntent.getBroadcast(
                    context,
                    calendars.indexOf(calendar),
                    notifyIntent,
                    PendingIntent.FLAG_ONE_SHOT
                )
            )
        }
    }

    //generates the requested number of calendar objects at 5 second intervals
    fun generateCalendars(quantity: Int): List<Calendar> {
        if (quantity < 1) {
            throw Exception("check value passed to generateCalendars")
        }

        val output = mutableListOf<Calendar>()

        var counter = 5
        for (i in (1..quantity)) {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.SECOND, get(Calendar.SECOND) + counter)
            }
            output.add(calendar)
            counter += 5
        }

        return output
    }
}