package com.example.stranded

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.stranded.chatpage.Set
import com.example.stranded.database.PromptLine

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