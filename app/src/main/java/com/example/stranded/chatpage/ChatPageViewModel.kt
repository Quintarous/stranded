package com.example.stranded.chatpage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stranded.Repository
import com.example.stranded.database.PromptLine
import com.example.stranded.database.ScriptLine
import com.example.stranded.notifyObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatPageViewModel @Inject constructor (private val repository: Repository): ViewModel() {

    val userSave = repository.userSave
    lateinit var sequence: Sequence

    private val _chatDataset = MutableLiveData(mutableListOf<ScriptLine>())
    val chatDataset: LiveData<MutableList<ScriptLine>>
        get() = _chatDataset

    private val _consoleDataset = MutableLiveData<MutableList<String>>(mutableListOf())
    val consoleDataset: LiveData<MutableList<String>>
        get() = _consoleDataset

    private val _promptDataset = MutableLiveData<MutableList<PromptLine>>(mutableListOf())
    val promptDataset: LiveData<MutableList<PromptLine>>
        get() = _promptDataset

    private lateinit var lastLine: ScriptLine

    init {
        //grabbing the sequence from the repository
        viewModelScope.launch {
            sequence = repository.getSequence(userSave.value?.sequence ?: 1)
            Log.i("bruh", "$sequence")

            //running the first line from the sequence
            if (sequence.scriptLines.isNotEmpty()) displayScriptLine(sequence.scriptLines[0])
        }
    }

    //callback that displays the next line/prompt when the user taps the chat recycler view
    fun userTouch() {
        when (lastLine.nextType) {
            //TODO implement an actual procedure for ending a narrative sequence
            "end" -> return //do nothing
            "script" -> displayScriptLine(sequence.scriptLines[lastLine.next - 1])
            else -> displayPromptSet(sequence.sets[lastLine.next - 1])
        }
    }

    //callback that displays the next line/prompt when the user taps on a prompt button
    fun promptSelected(index: Int) {
        val promptLine = promptDataset.value!![index]

        if (promptLine.nextType == "end") return

        _promptDataset.value!!.clear()
        _promptDataset.notifyObserver()

        displayScriptLine(ScriptLine(0, userSave.value!!.sequence, "user", promptLine.line, 0))

        when (promptLine.nextType) {
            "script" -> displayScriptLine(sequence.scriptLines[promptLine.next - 1])
            else -> displayPromptSet(sequence.sets[promptLine.next - 1])
        }
    }

    private fun displayScriptLine(scriptLine: ScriptLine) {
        //displaying console lines
        when (scriptLine.type) {
            "console" -> {
                _consoleDataset.value!!.add(scriptLine.line)
                lastLine = scriptLine
                _consoleDataset.notifyObserver()
            }

            "user" -> {
                _chatDataset.value!!.add(scriptLine)
                _chatDataset.notifyObserver()
            }

            else -> {
                _chatDataset.value!!.add(scriptLine)
                lastLine = scriptLine
                _chatDataset.notifyObserver()
            }
        }
    }

    private fun displayPromptSet(set: Set) {
        _promptDataset.value = set.lines
        _promptDataset.notifyObserver()
    }
}