package com.example.stranded

import android.content.Context
import android.graphics.Canvas
import android.text.SpannableString
import android.text.Spanned
import android.util.AttributeSet
import android.util.Log
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.getSpans
import androidx.core.view.ViewCompat
import believe.cht.fadeintextview.TextViewListener
// TODO this custom view is not being inflated (it's coming in as null in the adapters view binding
class CustomTextView(context: Context, attributeSet: AttributeSet): AppCompatTextView(context) {

    private var start: Long = 0
    private val letterDuration = 250
    private var isAnimating = false
    private lateinit var characterSequence: CharSequence
    private lateinit var spannableString: SpannableString
    private var textViewListener: TextViewListener? = null


    override fun setText(text: CharSequence, type: BufferType?) {

        characterSequence = text
        spannableString = SpannableString(text)

        val letters = spannableString.getSpans(0, spannableString.length, Letter::class.java)
        for (letter in letters) {
            spannableString.removeSpan(letter)
        }

        val length = spannableString.length

        if (length == 0) {
            super.setText(text, type)
            return
        }

        // alerting the listener if we have one
        textViewListener?.onTextStart()

        // attaching the Letter class to every character in the string
        for (index in (0..length)) {

            spannableString
                .setSpan(Letter(), index, index + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }

        super.setText(spannableString, BufferType.SPANNABLE)

        isAnimating = true
        start = AnimationUtils.currentAnimationTimeMillis()
        ViewCompat.postInvalidateOnAnimation(this)
    }


    override fun onDraw(canvas: Canvas) {

        super.onDraw(canvas)

        if(isAnimating) {

            val delta = AnimationUtils.currentAnimationTimeMillis() - start

            val letters: Array<Letter> = spannableString.getSpans(
                0,
                spannableString.length,
                Letter::class.java
            )

            val length = letters.size

            for (index in (0..length)) {

                if (delta >= (index * letterDuration)) letters[index].setAlpha(1.0f)

                else letters[index].setAlpha(0.0f)
            }

            if (letters.last().getAlpha() == 1.0f) {
                isAnimating = false
                textViewListener?.onTextFinish()
            }

            else ViewCompat.postInvalidateOnAnimation(this)
        }
    }


    fun setListener(listener: TextViewListener) {
        textViewListener = listener
    }
}