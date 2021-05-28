package com.example.stranded

import android.icu.util.Calendar
import android.util.Log

class NotificationTester {

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