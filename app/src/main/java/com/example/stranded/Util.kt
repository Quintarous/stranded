package com.example.stranded

import android.graphics.drawable.AnimationDrawable
import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.stranded.chatpage.Set
import com.example.stranded.database.PromptLine
import kotlinx.coroutines.*

fun createSetsList(sets: List<PromptLine>): MutableList<Set> {
    var count = 0
    val list = mutableListOf<Set>()

    for (line in sets) { if (line.set > count) count = line.set }

    for (i in (1..count)) { list.add(Set(i, mutableListOf())) }

    for (line in sets) { list[line.set - 1].lines.add(line) }

    return list
}

fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}

fun AnimationDrawable.onAnimationFinished(block: () -> Unit) {
    var duration: Long = 0
    for (i in 0..numberOfFrames) {
        duration += getDuration(i)
    }

    GlobalScope.launch {
        delay(duration + 200)
        block()
    }
}