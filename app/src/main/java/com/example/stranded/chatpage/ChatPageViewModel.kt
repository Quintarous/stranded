package com.example.stranded.chatpage

import android.util.Log
import androidx.lifecycle.*
import com.example.stranded.CustomTextView
import com.example.stranded.Repository
import com.example.stranded.database.*
import com.example.stranded.notifyObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatPageViewModel @Inject constructor (private val repository: Repository): ViewModel() {

    val userSave = repository.userSave
    lateinit var sequence: Sequence
    lateinit var promptResults: List<Int>

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

    //need this to have the fragment schedule work for us
    val scheduleNotification = MutableLiveData(false)

    lateinit var lastLine: ScriptLine

    private val scriptTriggers: MutableList<Trigger> = mutableListOf()
    private val  promptTriggers: MutableList<Trigger> = mutableListOf()

    init {
        //grabbing the sequence and prompt results from the repository
        viewModelScope.launch {
            //TODO remove this it's for the in memory database to populate
            delay(1000)
            sequence = repository.getSequence(userSave.value?.sequence ?: 1)
            promptResults = repository.getPromptResults().map { it.result }

            //restoring the user to their last save point or just displaying line 1 if they haven't
            //yet started the sequence
            restoreSave()

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

        if (userSave.line <= 1) {
            displayScriptLine(sequence.scriptLines[0])
            return
        }

        //adding all the script lines
        for (scriptLine in sequence.scriptLines) {


            when (scriptLine.type) {
                "console" -> _consoleDataset.value!!.add(scriptLine.line)

                else -> _chatDataset.value!!.add(scriptLine)
            }

            //if the next script line we would display is greater than the save point break the loop
            if (userSave.lineType == "script" && scriptLine.id == userSave.line) {
                lastLine = scriptLine
                break
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
                    val promptLine: PromptLine = set.lines[promptResults[set.number - 1]]

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
    fun userTouch(textView: CustomTextView) {

        if (textView.getAnimationStatus()) {

            textView.skipAnimation()
        } else {

            when (lastLine.nextType) {
                "end" -> {
                    viewModelScope.launch {
                        val sequence = userSave.value!!.sequence

                        //if the current sequence is not the last one
                        if (sequence < 8) {
                            repository.updateUserSaveData(
                                UserSave(1, false, sequence + 1, 0)
                            )

                            repository.clearPromptResult() // clearing the users saved choices

                            //telling the fragment to schedule notification + powerOn work
                            scheduleNotification.value = true
                        }

                        //else TODO do something when the user completes the story
                    }
                }

                "script" -> displayScriptLine(sequence.scriptLines[lastLine.next - 1])

                else -> displayPromptSet(sequence.sets[lastLine.next - 1])
            }
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
            //TODO find an equivalent way to do this like with the script line version or remove the ability to fire triggers on prompts
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

        //updating the PromptResult database table
        viewModelScope.launch {
            repository.insertPromptResult(index)
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
            if (trigger.triggerId == sequence.scriptLines.indexOf(scriptLine) + 1) {
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