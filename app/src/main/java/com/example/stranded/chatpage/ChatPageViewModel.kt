package com.example.stranded.chatpage

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

    private val _chatDataset = MutableLiveData(mutableListOf<String>())
    val chatDataset: LiveData<MutableList<String>>
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

            if (sequence.scriptLines.isNotEmpty()) displayScriptLine(sequence.scriptLines[0])
        }
    }
    //TODO add functionality for the user to interact with prompts and add infrastructure in the
    //viewmodel so when they do the app takes the appropriate action (ie: displays the next line)
    fun userTouch() {
        if (lastLine.nextType == "script") displayScriptLine(sequence.scriptLines[lastLine.next])
        else displayPromptSet(sequence.sets[lastLine.next])
    }

    private fun displayScriptLine(scriptLine: ScriptLine) {
        if (scriptLine.consoleLine) {
            _consoleDataset.value!!.add(scriptLine.line)
            lastLine = scriptLine
            _consoleDataset.notifyObserver()
        }

        else {
            _chatDataset.value!!.add(scriptLine.line)
            lastLine = scriptLine
            _chatDataset.notifyObserver()
        }
    }

    private fun displayPromptSet(set: Set) {
        _promptDataset.value = set.lines
        _promptDataset.notifyObserver()
    }
}