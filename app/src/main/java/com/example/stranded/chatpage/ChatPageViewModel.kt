package com.example.stranded.chatpage

import androidx.lifecycle.*
import com.example.stranded.CustomTextView
import com.example.stranded.Repository
import com.example.stranded.database.*
import com.example.stranded.notifyObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
// TODO set all triggers to fire off of ScriptLine ids instead of ScriptLine index locations (when db is 100% done)
@HiltViewModel
class ChatPageViewModel @Inject constructor (private val repository: Repository): ViewModel() {

    val userSave = repository.userSave
    lateinit var sequence: Sequence
    lateinit var promptResults: List<Int>

    private val _chatDataset = MutableLiveData(mutableListOf<ScriptLine>())
    val chatDataset: LiveData<MutableList<ScriptLine>>
        get() = _chatDataset

    val chatLastItemAnimated = MutableLiveData<Int>() // adapter observes this to tell if the last item has been animated already or not

    private val _consoleDataset = MutableLiveData<MutableList<String>>(mutableListOf())
    val consoleDataset: LiveData<MutableList<String>>
        get() = _consoleDataset

    val consoleLastItemAnimated = MutableLiveData<Int>() // adapter observes this to tell if the last item has been animated already or not

    private val _promptDataset = MutableLiveData<MutableList<PromptLine>>(mutableListOf())
    val promptDataset: LiveData<MutableList<PromptLine>>
        get() = _promptDataset

// observed live data for starting and stopping animations and sound effects
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

// need this to have the fragment schedule work for us
    val scheduleNotification = MutableLiveData(false)

    lateinit var lastLine: ScriptLine

    private val _letterDuration = MutableLiveData<Int>()
    val letterDuration: LiveData<Int>
        get() = _letterDuration


// function for setting the letterDuration both here and in the database
    fun setLetterDuration(value: Int) {

        _letterDuration.value = value // setting the new value

        // updating the user save so their preference is saved
        viewModelScope.launch {
            val newUserSave = repository.getUserSave().apply {
                this.letterDuration = value
            } ?: return@launch

            repository.updateUserSaveData(newUserSave)
        }
    }

    private val scriptTriggers: MutableList<Trigger> = mutableListOf()
    private val  promptTriggers: MutableList<Trigger> = mutableListOf()

    init {
        // grabbing the sequence, letterDuration and prompt results from the repository
        viewModelScope.launch {
            val userSave = repository.getUserSave()

            sequence = repository.getSequence(userSave.sequence)
            _letterDuration.value = userSave.letterDuration
            promptResults = repository.getPromptResults().map { it.result }

            // sorting the sequence triggers into script and prompt trigger lists
            for (trigger in sequence.triggers) {
                when (trigger.triggerType) {
                    "script" -> scriptTriggers.add(trigger)
                    else -> promptTriggers.add(trigger)
                }
            }

            // restoring the user to their last save point or just displaying line 1 if they haven't
            // yet started the sequence
            restoreSave()
        }
    }

// function to progress up to the user save point if further than the first script line
    suspend fun restoreSave() {
        val userSave = repository.getUserSave() // grabbing the userSave from the repository

// if we don't need to progress past the first line just display it and return
// TODO userSave.line uses absolute id's of lines this only works on the first sequence
        if (userSave.line <= 1) {
            displayScriptLine(sequence.scriptLines[0])
            return
        }

// adding all the script lines
        for (scriptLine in sequence.scriptLines) {

// look for triggers that need to be fired. only looping and stop command triggers are fired
// everything else is ignored
            for (trigger in scriptTriggers) {

                if (trigger.triggerId == sequence.scriptLines.indexOf(scriptLine) + 1) {

                    if (trigger.loop) fireTrigger(trigger) // fire if trigger is a looping one

                    else {
                        if (trigger.resourceId == null) {
                            fireTrigger(trigger) // or fire if trigger is a stop command
                        }
                    }
                }
            }

            when (scriptLine.type) { // adding ScriptLines to their respective recycler views
                "console" -> _consoleDataset.value!!.add(scriptLine.line)

                else -> _chatDataset.value!!.add(scriptLine)
            }

// if this script line is the save point break the loop
            if (userSave.lineType == "script" && scriptLine.id == userSave.line) {
                lastLine = scriptLine
                break
            }

// adding a user response in before the next script line if we need to
            if (scriptLine.nextType == "prompt") {
                val set: Set = sequence.sets[scriptLine.next - 1]

// if this is the prompt we need to stop on than simply display the prompt and break
                if (userSave.lineType == "prompt" && set.number == userSave.line) {
                    displayPromptSet(set)
                    break
                }

// else throw the saved user response in the chat window and keep on moving
                else {
                    val promptLine: PromptLine = set.lines[promptResults[set.number - 1]]

                    val generatedScriptLine = ScriptLine(
                        0,
                        userSave.sequence,
                        "user",
                        promptLine.line,
                        promptLine.next,
                        promptLine.nextType
                    )

                    _chatDataset.value!!.add(generatedScriptLine)

// stopping the loop if we need to stop on the prompt response
                    if (userSave.lineType == "response" && set.number == userSave.line) {
                        lastLine = generatedScriptLine
                        break
                    }
                }
            }
        }

        _chatDataset.notifyObserver()
        _consoleDataset.notifyObserver()
        _promptDataset.notifyObserver()
    }

// callback that displays the next line/prompt when the user taps the chat recycler view
// textView is the last line that was displayed
    fun userTouch(textView: CustomTextView?) {

        if (textView != null) {

            if (textView.getAnimationStatus()) { // skipping the animation if it's still running
                textView.skipAnimation()
                return
            }
        }

// displaying the next line/prompt set or ending the sequence
        when (lastLine.nextType) {
            "end" -> {
                viewModelScope.launch {
                    val sequence = repository.getUserSave().sequence

                    if (sequence < 8) { // if the current sequence is not the last one
                        repository.updateUserSaveData(
                            UserSave(1, false, letterDuration.value!!, sequence + 1, 0)
                        )

                        repository.clearPromptResult() // clearing the users saved choices
                        
// telling the fragment to schedule notification + powerOn work
                        scheduleNotification.value = true
                    }

                    // else TODO do something when the user completes the story
                }
            }

            "script" -> displayScriptLine(sequence.scriptLines[lastLine.next - 1])

            else -> displayPromptSet(sequence.sets[lastLine.next - 1])
        }
    }

    // callback that displays the next line/prompt when the user taps on a prompt button
    fun promptSelected(index: Int) {
        val promptLine = promptDataset.value!![index] // grabbing the selected prompt

        if (promptLine.nextType == "end") return // do nothing if this prompt ends the sequence

        _promptDataset.value!!.clear() // getting the prompt buttons off the screen
        _promptDataset.notifyObserver()

        // generating a ScriptLine object to display the users dialogue choice
        // TODO test if putting in 0 for the sequence breaks anything
        displayScriptLine(ScriptLine(0, 0, "user", promptLine.line, promptLine.next, promptLine.nextType))

        // checking for any triggers that need to be fired
        for (trigger in promptTriggers) {
            //TODO find an equivalent way to do this like with the script line version or remove the ability to fire triggers on prompts
            if (trigger.triggerId == promptLine.id) {
                fireTrigger(trigger)
            }
        }

// updating the PromptResult database table with the users dialogue choice
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

// checking to see if we need to fire any triggers on this script line and firing them if we do
        for (trigger in scriptTriggers) {
            if (trigger.triggerId == sequence.scriptLines.indexOf(scriptLine) + 1) {
                fireTrigger(trigger)
            }
        }

// updating the user save
        viewModelScope.launch {
// if the line being displayed is a user response leave the UserSave.line as the prompt set #
// and only change the lineType to "response" (to indicate the user ended on a prompt response
// as opposed to the prompt set itself)
            val userSave = repository.getUserSave()

            if (scriptLine.type == "user") {
                repository.updateUserSaveData(userSave.apply {
                    this.lineType = "response"
                })
// else update the user save with the most recent script lines data as normal
            } else {
                repository.updateUserSaveData(userSave.apply {
                    this.line = scriptLine.id
                    this.lineType = "script"
                })
            }
        }
    }

    private fun fireTrigger(trigger: Trigger) {
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

    private fun displayPromptSet(set: Set) {
        _promptDataset.value = set.lines
        _promptDataset.notifyObserver()

// updating the user save
        viewModelScope.launch {
            val userSave = repository.getUserSave()

            repository.updateUserSaveData(userSave.apply {
                this.line = set.number
                this.lineType = "prompt"
            })
        }
    }
}