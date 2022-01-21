package com.austin.stranded

import android.content.Context
import android.graphics.Canvas
import android.text.SpannableString
import android.text.Spanned
import android.util.AttributeSet
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import believe.cht.fadeintextview.TextViewListener


/**
 * CustomTextView is a custom view extending the default android TextView view (obvious I know).
 * The goal is to animate the text inside it so that it appears one character at a time with a
 * custom interval in between each character.
 *
 * The class also includes a method to skip the animation, a listener to execute code on
 * animation completion, and a method to set the letterDuration. (letterDuration is the wait time
 * in between every character appearing)
 */
class CustomTextView(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {

    private var start: Long = 0 // the start time of the animation (in androids animation time)
    private var letterDuration: Int // wait time in milliseconds between every character being shown
    private var isAnimating = false // determines whether the animation has completed or not

    private lateinit var characterSequence: CharSequence
    private lateinit var spannableString: SpannableString

    // an optional listener that runs when the animation is started and when it's completed
    private var textViewListener: TextViewListener? = null


    init {
        // grabbing the attributes from the AttributeSet
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.CustomTextView, 0, 0)

        // grabbing the custom value for letterDuration or just setting it to 70 if there is none
        try {
            letterDuration = typedArray.getInteger(R.styleable.CustomTextView_letterDuration, 70)
        } finally {
            typedArray.recycle()
        }
    }


    /**
     * Makes sure there is a letter class attached to every character in the string we're given,
     * tells the TextViewListener (if there is one) that the animation is starting, sets the
     * isAnimating property to true, and then calls postInvalidateOnAnimation to get android to
     * redraw the view.
     */
    override fun setText(text: CharSequence, type: BufferType?) {

        characterSequence = text
        spannableString = SpannableString(text)

        // cleaning all the letter classes off the characters
        val letters = spannableString.getSpans(0, spannableString.length, Letter::class.java)
        for (letter in letters) {
            spannableString.removeSpan(letter)
        }

        val length = spannableString.length // grab the length of the string

        // if it's an empty string we don't need to do anything
        if (length == 0) {
            super.setText(text, type)
            return
        }

        // alerting the TextViewListener that the animation has started if we have one
        textViewListener?.onTextStart()

        // attaching the Letter class to every character in the string
        for (index in (0 until length)) {

            spannableString
                .setSpan(Letter(), index, index + 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
        }

        super.setText(spannableString, BufferType.SPANNABLE)

        isAnimating = true // recording that the text animation is running

        // recording the animation start time and adding a short delay for aesthetics
        start = AnimationUtils.currentAnimationTimeMillis() + 400

        // invalidating ourself so android will redraw us
        ViewCompat.postInvalidateOnAnimation(this)
    }


    /**
     * onDraw is responsible for actually running the animation.
     */
    override fun onDraw(canvas: Canvas) {

        super.onDraw(canvas)

        if(isAnimating) {

            // delta is how much time has passed since the animation was started
            val delta = AnimationUtils.currentAnimationTimeMillis() - start

            // grabbing every Letter from the string
            val letters: Array<Letter> = spannableString.getSpans(
                0,
                spannableString.length,
                Letter::class.java
            )

            val length = letters.size // recording the size

            /**
             * This is where the magic happens! In short, every character has a time
             * (index * letterDuration) that the delta must reach before that character is set
             * from invisible to visible. So this for loop is going through every letter and
             * turning it on if enough time has passed and making sure it's turned off if not
             * enough time has passed.
             */
            for (index in (0 until length)) {

                if (delta >= (index * letterDuration)) {
                    letters[index].setAlpha(1.0f)
                } else {
                    letters[index].setAlpha(0.0f)
                }
            }

            // if the last letter in the string is visible
            if (letters.last().getAlpha() == 1.0f) {
                // then our animation is complete!
                isAnimating = false
                textViewListener?.onTextFinish()
                ViewCompat.postInvalidateOnAnimation(this)
            }

            /**
             * Calling this simply makes android redraw the view. Since we are doing it from
             * within onDraw, it essentially becomes a recursive method that runs until the
             * animation is finished!
             */
            else ViewCompat.postInvalidateOnAnimation(this)
        }
    }


    /**
     * Immediately makes every letter visible essentially "skipping" the animation
     */
    fun skipAnimation() {

        if (isAnimating) {

            isAnimating = false

            // getting all the letters
            val letters = spannableString
                .getSpans(0, spannableString.length, Letter::class.java)

            // setting every one to visible
            for (letter in letters) {
                letter.setAlpha(1.0f)
            }

            textViewListener?.onTextFinish() // telling the TextViewListener we're done

            // and finally getting android to redraw this view to reflect our changes
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }


    fun getAnimationStatus() = isAnimating


    fun setListener(listener: TextViewListener) {
        textViewListener = listener
    }

    fun setLetterDuration(duration: Int) {
        letterDuration = duration
    }
}