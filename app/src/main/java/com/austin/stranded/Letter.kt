package com.austin.stranded

import android.graphics.Color
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.text.style.UpdateAppearance

/**
 * Used by CustomTextView to edit the alpha(opacity) value of individual characters in it's string
 */
class Letter: CharacterStyle(), UpdateAppearance {

    private var mAlpha = 0.0f

    fun setAlpha(alpha: Float) { mAlpha = alpha }

    fun getAlpha(): Float = mAlpha

    override fun updateDrawState(tp: TextPaint?) {
        val red = Color.red(Color.WHITE)
        val green = Color.green(Color.WHITE)
        val blue = Color.blue(Color.WHITE)
        val alpha = Color.alpha(Color.WHITE)

        val color = Color.argb( (alpha * mAlpha).toInt(), red, green, blue )
        tp?.color = color
    }
}