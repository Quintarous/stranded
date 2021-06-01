package com.example.stranded

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //creating and registering the notification testing channel
        //DELETE ONCE DONE TESTING
        createChannel(this, "test", "Test Channel")

        val notificationTester = NotificationTester(this)

        notificationTester.scheduleNotifications(2)
    }
}