package com.example.stranded.chatpage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stranded.Repository
import com.example.stranded.database.PromptLine
import com.example.stranded.database.ScriptLine
import com.example.stranded.database.Trigger
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

    //observed live data for starting and stopping animations and sound effects
    private val _stopSound = MutableLiveData<Trigger>()
    val stopSound: LiveData<Trigger>
        get() = _stopSound

    private val _startSound = MutableLiveData<Trigger>()
    val startSound: LiveData<Trigger>
        get() = _startSound

    private val _startSoundOneAndDone = MutableLiveData<Trigger>()
    val startSoundOneAndDone: LiveData<Trigger>
        get() = _startSoundOneAndDone

    private val _stopAnim = MutableLiveData<Trigger>()
    val stopAnim: LiveData<Trigger>
        get() = _stopAnim

    private val _startAnim = MutableLiveData<Trigger>()
    val startAnim: LiveData<Trigger>
        get() = _startAnim

    private val _startAnimOneAndDone = MutableLiveData<Trigger>()
    val startAnimOneAndDone: LiveData<Trigger>
        get() = _startAnimOneAndDone

    private lateinit var lastLine: ScriptLine

    private lateinit var scriptTriggers: MutableList<Trigger>
    private lateinit var promptTriggers: MutableList<Trigger>

    init {
        //grabbing the sequence from the repository
        viewModelScope.launch {
            sequence = repository.getSequence(userSave.value?.sequence ?: 1)

            //running the first line from the sequence
            if (sequence.scriptLines.isNotEmpty()) displayScriptLine(sequence.scriptLines[0])

            //sorting the sequence triggers into script and prompt trigger lists
            for (trigger in sequence.triggers) {
                when (trigger.triggerType) {
                    "Script" -> scriptTriggers.add(trigger)
                    else -> promptTriggers.add(trigger)
                }
            }
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

        //displaying the user selected prompt in the chat recycler
        displayScriptLine(ScriptLine(0, userSave.value!!.sequence, "user", promptLine.line, 0))

        when (promptLine.nextType) {
            "script" -> displayScriptLine(sequence.scriptLines[promptLine.next - 1])
            else -> displayPromptSet(sequence.sets[promptLine.next - 1])
        }
    }

    //TODO add the trigger checks to this function
    private fun displayScriptLine(scriptLine: ScriptLine) {
        //displaying console lines
        when (scriptLine.type) {
            "console" -> {
                _consoleDataset.value!!.add(scriptLine.line)
                lastLine = scriptLine
                _consoleDataset.notifyObserver()
            }

            else -> {
                _chatDataset.value!!.add(scriptLine)
                lastLine = scriptLine
                _chatDataset.notifyObserver()
            }
        }

        //TODO test this to see if it's working!
        //checking to see if we need to fire any triggers on this script line and firing them if we do
        for (trigger in scriptTriggers) {
            if (trigger.triggerId == scriptLine.id) {
                when (trigger.resourceType) {
                    "sound" -> {
                        if (trigger.resourceId == null) {
                            _stopSound.value = trigger
                            _stopSound.notifyObserver()
                        }
                        else {
                            if (trigger.oneAndDone!!) {
                                _startSoundOneAndDone.value = trigger
                                _startSoundOneAndDone.notifyObserver()
                            }
                            else {
                                _startSound.value = trigger
                                _startSound.notifyObserver()
                            }
                        }
                    }

                    "animation" -> {
                        if (trigger.resourceId == null) {
                            _stopAnim.value = trigger
                            _stopAnim.notifyObserver()
                        }
                        else {
                            if (trigger.oneAndDone!!) {
                                _startAnimOneAndDone.value = trigger
                                _startAnimOneAndDone.notifyObserver()
                            }
                            else {
                                _startAnim.value = trigger
                                _startAnim.notifyObserver()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun displayPromptSet(set: Set) {
        _promptDataset.value = set.lines
        _promptDataset.notifyObserver()
    }
}