package com.example.stranded

import android.graphics.drawable.AnimationDrawable

class AnimationDrawable3(animDrawable: AnimationDrawable) : AnimationDrawable() {

    init {
        for (i in (0..animDrawable.numberOfFrames)) {
            addFrame(animDrawable.getFrame(i), animDrawable.getDuration(i))
        }
    }

    interface IAnimationFinishListener {
        fun onAnimationFinished()
    }

    private var finished = false
    var animationFinishListener: IAnimationFinishListener? = null

    override fun selectDrawable(idx: Int): Boolean {
        val ret = super.selectDrawable(idx)

        if (idx != 0 && idx == numberOfFrames - 1) {
            if (!finished) {
                finished = true
                if (animationFinishListener != null) animationFinishListener!!.onAnimationFinished()
            }
        }

        return ret
    }
}