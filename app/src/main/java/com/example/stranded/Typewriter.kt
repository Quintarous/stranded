package com.example.stranded

import android.content.Context
import android.widget.TextView

class Typewriter(context: Context): androidx.appcompat.widget.AppCompatTextView(context) {

    fun animateText(text: String) {
        setText("")

        for (character in text) {
        }
    }
}