package com.example.stranded

import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var testAnimation: AnimationDrawable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_chat_page)

        //creating and registering the notification testing channel
        //DELETE ONCE DONE TESTING
        createChannel(this, "test", "Test Channel")

        val testImage = findViewById<ImageView>(R.id.testAnimation).apply {
            setBackgroundResource(R.drawable.test_animation)
            testAnimation = background as AnimationDrawable
        }

        testImage.setOnClickListener { testAnimation.start() }
    }
}