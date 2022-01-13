package com.example.stranded

import android.graphics.drawable.AnimationDrawable
import androidx.lifecycle.MutableLiveData
import com.example.stranded.chatpage.Set
import com.example.stranded.database.PromptLine
import kotlinx.coroutines.*

/**
 * Utility function that takes a list of PromptLines (essentially what comes out of the database)
 * and sorts them into Set objects.
 */
fun createSetsList(promptLines: List<PromptLine>): MutableList<Set> {

    var setCount = 0 // this will equal how many sets are present in the dataset
    val outputList = mutableListOf<Set>() // output of the function

    /**
     * Here we are getting a count of how many Set objects we'll need. The loop goes through every
     * PromptLine and puts the highest PromptLine.set number it sees into setCount.
     */
    for (line in promptLines) { if (line.set > setCount) setCount = line.set }

    /**
     * Now that we know how many Sets there are we can add all the empty Set objects to our
     * output list.
     */
    for (i in (1..setCount)) { outputList.add(Set(i, mutableListOf())) }

    /**
     * Once outputList is populated with empty Sets we can iterate through the PromptLines again
     * and add them to their respective Sets.
     */
    for (line in promptLines) { outputList[line.set - 1].lines.add(line) }

    // and finally returning the list
    return outputList
}


/**
 * Does what it says on the tin.
 *
 * NOTE: In case your wondering why this is necessary. The main LiveData objects this app uses
 * contain MutableLists. And when you update a MutableList that is inside LiveData, the LiveData
 * doesn't even notice anything has changed. Since it's still got the same list object inside of
 * it, as far as the LiveData is concerned nothing is different.
 *
 * So to solve it we're just setting the value of the live data to itself which triggers all the
 * observers!
 */
fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}


/**
 * Takes a block of code and runs it when the animation it was called on has finished.
 *
 * This is done by calculating the duration of the animation and waiting that amount of time
 * (+200 milliseconds for buffer) then running the code block we were given.
 */
suspend fun AnimationDrawable.onAnimationFinished(block: () -> Unit) {

    var duration: Long = 0

    // calculating the duration
    for (i in 0 until numberOfFrames) {
        duration += getDuration(i)
    }

    delay(duration + 200) // waiting for it to finish

    block() // running the code block
}