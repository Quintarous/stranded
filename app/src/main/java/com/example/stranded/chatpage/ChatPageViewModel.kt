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

    private val scriptTriggers: MutableList<Trigger> = mutableListOf()
    private val  promptTriggers: MutableList<Trigger> = mutableListOf()

    init {
        //grabbing the sequence from the repository
        viewModelScope.launch {
            sequence = repository.getSequence(userSave.value?.sequence ?: 1)

            //running the first line from the sequence
            if (sequence.scriptLines.isNotEmpty()) displayScriptLine(sequence.scriptLines[0])

            //sorting the sequence triggers into script and prompt trigger lists
            for (trigger in sequence.triggers) {
                when (trigger.triggerType) {
                    "script" -> scriptTriggers.add(trigger)
                    else -> promptTriggers.add(trigger)
                }
            }
        }
    }

    //function to progress up to the user save point if further than the first script line
    fun restoreSave() {
        val userSave = userSave.value!!

        if (userSave.line == 1) return

        //adding all the script lines
        for (scriptLine in sequence.scriptLines) {

            //if the next script line we would display is greater than the save point break the loop
            if (userSave.lineType == "script" && scriptLine.id > userSave.line) break

            when (scriptLine.type) {
                "console" -> _consoleDataset.value!!.add(scriptLine.line)

                else -> _chatDataset.value!!.add(scriptLine)
            }

            //adding a user response in before the next script line if we need to
            if (scriptLine.nextType == "prompt") {
                val set: Set = sequence.sets[scriptLine.next - 1]

                //if this is the prompt we need to stop on than simply display the prompt and break
                if (userSave.lineType == "prompt" && set.number == userSave.line) {
                    displayPromptSet(set)
                    break
                }

                //else throw the saved user response in the chat window and keep on moving
                else {
                    val promptLine: PromptLine = set.lines[userSave.promptChoices[set.number - 1]]

                    _chatDataset.value!!
                        .add(ScriptLine(0, userSave.sequence, "user", promptLine.line, 0))
                }
            }
        }

        _chatDataset.notifyObserver()
        _consoleDataset.notifyObserver()
        _promptDataset.notifyObserver()
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

        //checking for any triggers that need to be fired
        for (trigger in promptTriggers) {
            if (trigger.triggerId == promptLine.id) {
                when (trigger.resourceType) {
                    "sound" -> {
                        if (trigger.resourceId == null) {
                            _stopSound.value = trigger
                        }
                        else {
                            if (trigger.oneAndDone) {
                                _startSoundOneAndDone.value = trigger
                            }
                            else {
                                _startSound.value = trigger
                            }
                        }
                    }

                    "animation" -> {
                        if (trigger.resourceId == null) {
                            _stopAnim.value = trigger
                        }
                        else {
                            if (trigger.oneAndDone) {
                                _startAnimOneAndDone.value = trigger
                            }
                            else {
                                _startAnim.value = trigger
                            }
                        }
                    }
                }
            }
        }

        //displaying the next script line or prompt
        when (promptLine.nextType) {
            "script" -> displayScriptLine(sequence.scriptLines[promptLine.next - 1])
            else -> displayPromptSet(sequence.sets[promptLine.next - 1])
        }

        //updating the user save
        viewModelScope.launch {
            repository.updateUserSaveData(userSave.value!!.apply {
                this.promptChoices.add(index)
            })
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

            else -> {
                _chatDataset.value!!.add(scriptLine)
                lastLine = scriptLine
                _chatDataset.notifyObserver()
            }
        }

        //checking to see if we need to fire any triggers on this script line and firing them if we do
        for (trigger in scriptTriggers) {
            if (trigger.triggerId == scriptLine.id) {
                when (trigger.resourceType) {
                    "sound" -> {
                        if (trigger.resourceId == null) {
                            _stopSound.value = trigger
                        }
                        else {
                            if (trigger.oneAndDone) {
                                _startSoundOneAndDone.value = trigger
                            }
                            else {
                                _startSound.value = trigger
                            }
                        }
                    }

                    "animation" -> {
                        if (trigger.resourceId == null) {
                            _stopAnim.value = trigger
                        }
                        else {
                            if (trigger.oneAndDone) {
                                _startAnimOneAndDone.value = trigger
                            }
                            else {
                                _startAnim.value = trigger
                            }
                        }
                    }
                }
            }
        }

        //updating the user save
        viewModelScope.launch {
            repository.updateUserSaveData(userSave.value!!.apply {
                this.line = scriptLine.id
                this.lineType = "script"
            })
        }
    }

    private fun displayPromptSet(set: Set) {
        _promptDataset.value = set.lines
        _promptDataset.notifyObserver()

        //updating the user save
        viewModelScope.launch {
            repository.updateUserSaveData(userSave.value!!.apply {
                this.line = set.number
                this.lineType = "prompt"
            })
        }
    }
}