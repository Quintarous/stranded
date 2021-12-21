package com.example.stranded.chatpage

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.*
import com.example.stranded.CustomTextView
import com.example.stranded.Repository
import com.example.stranded.database.*
import com.example.stranded.notifyObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
// TODO song in sequence 7 doesn't restart cuz it's not looping
// TODO read and comment all code
// TODO mix the volume of all the sound effects
// TODO clean up all the livedata references that were replaced by eventChannel
// TODO add the github link to the about page when code is final
@HiltViewModel
class ChatPageViewModel @Inject constructor (private val repository: Repository): ViewModel() {

    lateinit var sequence: Sequence
    lateinit var promptResults: List<Int>
    lateinit var lastLine: ScriptLine

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


    private val _letterDuration = MutableLiveData<Int>()
    val letterDuration: LiveData<Int>
        get() = _letterDuration

    private val scriptTriggers: MutableList<Trigger> = mutableListOf()
    private val  promptTriggers: MutableList<Trigger> = mutableListOf()

    private var lastAnimTrigger: Trigger? = null
    private var lastSoundTrigger: Trigger? = null

    private val _userSaveFlow = MutableStateFlow<UserSave?>(null)
    val userSaveFlow: StateFlow<UserSave?> = _userSaveFlow

    var mediaPlayer: MediaPlayer? = null

    init {
        // grabbing the sequence, letterDuration and prompt results from the repository
        viewModelScope.launch {
            collectUserSave()

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
            restoreSave(0, "script", 0, userSave) // restoring the user to their last save point if needed
        }
    }

    // collecting the userSaveFlow from the repository and emitting values to _userSaveFlow
    private fun collectUserSave() {
        viewModelScope.launch {
            repository.userSaveFlow.stateIn(viewModelScope).collect{ userSave ->
                _userSaveFlow.emit(userSave)
            }
        }
    }

    // holder class for one shot events that are sent to the ChatPageFragment
    sealed class Event {
        object NavToNoPower: Event()
        object NavToPowerOn: Event()
        object NavToEnding: Event()
        object ScheduleNotification: Event()
        object StopSound: Event()
        data class StartSound(val trigger: Trigger): Event()
        data class StartSoundOneAndDone(val oldTrigger: Trigger, val newTrigger: Trigger): Event()
        object StopAnim: Event()
        data class StartAnim(val trigger: Trigger): Event()
        data class StartAnimOneAndDone(val oldTrigger: Trigger, val newTrigger: Trigger): Event()
    }

    private val eventChannel = Channel<Event>(2, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val eventFlow = eventChannel.receiveAsFlow()


    fun startupNavigationCheck(fromPowerOn: Int) {
        viewModelScope.launch {
            val userSave = repository.getUserSave()

            if (userSave.isPowered) { // isPowered is true

                if (userSave.line == 0) { // if the sequence has not yet been started

                    if (fromPowerOn == userSave.sequence) { // if the "Power On" button was pressed
                        startSequence() // only now can we start the sequence
                    } else { // "Power On" button was NOT pressed
                        eventChannel.trySend(Event.NavToPowerOn) // user needs to hit "Power On" first
                    }
                }

            } else { // isPowered is false
                eventChannel.trySend(Event.NavToNoPower)
            }
        }
    }

// function for setting the letterDuration both here and in the database
    fun setLetterDuration(value: Int) {

        _letterDuration.value = value // setting the new value

        // updating the user save so their preference is saved
        viewModelScope.launch {
            val newUserSave = repository.getUserSave().apply {
                this.letterDuration = value
            }

            repository.updateUserSaveData(newUserSave)
        }
    }

// function for updating the demoMode value in the UserSave
    fun setDemoMode(value: Boolean) {
        viewModelScope.launch {
            val oldUserSave = repository.getUserSave()

            val newUserSave = oldUserSave.apply {
                demoMode = value
            }

            repository.updateUserSaveData(newUserSave)
        }
    }

    fun startSequence() = displayScriptLine(sequence.scriptLines[0]) // starts the sequence with the first ScriptLine

    /** restoreSave is a recursive method that progresses the app to whatever line the user was on
     according the the UserSave line and lineType.

    lineIndex: the index of a ScriptLine or prompt set.

    lineType: either "script" or "prompt" this determines whether lineIndex is referring to a
    ScriptLine or prompt set.

    promptResultIndex: (starts at 0) used by restoreSave() to keep track of which prompt result
    value it needs to look at when recreating the users choices.
    */
    private fun restoreSave(lineIndex: Int, lineType: String, promptResultIndex: Int, userSave: UserSave) {
        if (userSave.line == 0) return // if we're on line 0 then there's nothing to do so return

        when(lineType) { // different logic needs to be run if we're given a ScriptLine vs a set number
            "script" -> {
                val scriptLine = sequence.scriptLines[lineIndex] // grabbing the ScriptLine object

                for (trigger in scriptTriggers) {
                    // if the trigger is supposed to fire on this scriptLine
                    if (trigger.triggerId == sequence.scriptLines.indexOf(scriptLine) + 1) {
                        //TODO consider firing all triggers so oneAndDone and non looping triggers still play when the user comes back after a restart
                        // only fire if it's a looping or stop trigger (ignores every other type of trigger)
                        if (trigger.loop) fireTrigger(trigger)
                        else {
                            if (trigger.resourceId == null) fireTrigger(trigger)
                        }
                    }
                }

                lastLine = scriptLine // caching last line displayed is required for the fragment to function

                when (scriptLine.type) { // displaying the ScriptLine
                    "console" -> _consoleDataset.value!!.add(scriptLine.line)

                    else -> _chatDataset.value!!.add(scriptLine)
                }

                // if this script line is the stopping point then return
                if (userSave.lineType == "script" && scriptLine.id == userSave.line) {
                    _chatDataset.notifyObserver()
                    _consoleDataset.notifyObserver()
                    _promptDataset.notifyObserver()
                    return
                }

                // this is the recursive part. after displaying the line and knowing it's not the
                // stopping point we gotta move on and do the same thing for the next line/prompt.
                restoreSave(
                    scriptLine.next - 1,
                    scriptLine.nextType,
                    promptResultIndex,
                    userSave
                )
                return
            }

            else -> { // this runs if restoreSave() is called with a set number
                val set = sequence.sets[lineIndex] // grabbing the set object

                for (trigger in promptTriggers) { // same trigger logic as before!
                    // if the trigger is supposed to fire on this scriptLine
                    if (trigger.triggerId == set.number) {

                        // only fire if it's a looping or stop trigger (ignores every other type of trigger)
                        if (trigger.loop) fireTrigger(trigger)
                        else {
                            if (trigger.resourceId == null) fireTrigger(trigger)
                        }
                    }
                }

                // if this prompt set is our stopping point then display it and return
                if (userSave.lineType == "prompt" && userSave.line == set.number) {
                    displayPromptSet(set)
                    _chatDataset.notifyObserver()
                    _consoleDataset.notifyObserver()
                    _promptDataset.notifyObserver()
                    return
                }

                // if we don't stop on the prompt we'll need to display whatever choice the user
                // picked! So we'll use the promptResults table to determine which option they chose.
                val promptLine = set.lines[promptResults[promptResultIndex]]

                // the _chatDataset only takes ScriptLines so we'll have to make one in order to
                // display our promptLine
                val generatedScriptLine = ScriptLine(
                    0,
                    userSave.sequence,
                    "user",
                    promptLine.line,
                    promptLine.next,
                    promptLine.nextType
                )

                _chatDataset.value!!.add(generatedScriptLine) // displaying the promptLine

                lastLine = generatedScriptLine // gotta cache the last line displayed as usual

                // now we need to check if this response is the stopping point. If it is we can return.
                if (userSave.lineType == "response" && userSave.line == set.number) {
                    _chatDataset.notifyObserver()
                    _consoleDataset.notifyObserver()
                    _promptDataset.notifyObserver()
                    return
                }

                // if we've made it this far without returning then we know both this prompt set
                // and the response are not the stopping point. So we keep going!
                restoreSave(
                    promptLine.next - 1, promptLine.nextType,
                    promptResultIndex + 1,
                    userSave
                )
                return
            }
        }
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
                    val currentUserSave = repository.getUserSave()

                    if (currentUserSave.sequence < 8) { // if the current sequence is not the last one

                        val newUserSave = currentUserSave.apply { // updating the user save
                            isPowered = false
                            sequence += 1
                            line = 0
                        }
                        repository.updateUserSaveData(newUserSave) // pushing the change to the db

                        prepForNextSequence() // clearing the viewModel for the next sequence

                        repository.clearPromptResult() // clearing the users saved choices

// telling the fragment to schedule notification + powerOn work
                        if (!newUserSave.demoMode) {
                            eventChannel.trySend(Event.ScheduleNotification)
                        }

                        eventChannel.trySend(Event.NavToNoPower)
                    } else {
                        val clearedUserSave = UserSave(1, true, 50, 1,
                            0, "script", false)

                        repository.updateUserSaveData(clearedUserSave)
                        prepForNextSequence()
                        repository.clearPromptResult()

                        eventChannel.trySend(Event.NavToEnding)
                    }
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

// takes a ScriptLine object and displays it
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
            // if the line being displayed is a user response leave the UserSave.line as the prompt
            // set # and only change the lineType to "response" (to indicate the user ended on a
            // prompt response as opposed to the prompt set itself)
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

    private fun fireTrigger(trigger: Trigger) { // tells the fragment to fire the trigger
        when (trigger.resourceType) {
            "sound" -> {
                if (trigger.resourceId == null) {
                    eventChannel.trySend(Event.StopSound)
                    //_stopSound.value = trigger
                } else {
                    if (trigger.oneAndDone) {
                        if (lastSoundTrigger != null) {
                            eventChannel.trySend(Event.StartSoundOneAndDone(lastSoundTrigger!!, trigger))
                            //_startSoundOneAndDone.value = trigger
                        } else {
                            Log.i("bruh" ,"tried to send Event.StartSoundOneAndDone but lastSoundTrigger was null")
                        }
                    }
                    else {
                        eventChannel.trySend(Event.StartSound(trigger))
                        //_startSound.value = trigger
                    }
                }

                lastSoundTrigger = trigger
            }

            "animation" -> {
                if (trigger.resourceId == null) {
                    eventChannel.trySend(Event.StopAnim)
                    //_stopAnim.value = trigger
                }
                else {
                    if (trigger.oneAndDone) {
                        if (lastAnimTrigger != null) {
                            eventChannel.trySend(Event.StartAnimOneAndDone(lastAnimTrigger!!, trigger))
                            //_startAnimOneAndDone.value = trigger
                        } else {
                            Log.i("bruh" ,"tried to send Event.StartAnimOneAndDone but lastAnimTrigger was null")
                        }
                    }
                    else {
                        eventChannel.trySend(Event.StartAnim(trigger))
                        //_startAnim.value = trigger
                    }
                }

                lastAnimTrigger = trigger
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

    private fun prepForNextSequence() {
// clearing all the cached data from the last sequence
        _chatDataset.value = mutableListOf()
        _consoleDataset.value = mutableListOf()
        chatLastItemAnimated.value = 0
        consoleLastItemAnimated.value = 0
        scriptTriggers.clear()
        promptTriggers.clear()
        promptResults = listOf()
        lastSoundTrigger = null
        lastAnimTrigger = null

// setting up required data to run the next sequence
        viewModelScope.launch {
            val userSave = repository.getUserSave()

            sequence = repository.getSequence(userSave.sequence)

            for (trigger in sequence.triggers) {
                when (trigger.triggerType) {
                    "script" -> scriptTriggers.add(trigger)
                    else -> promptTriggers.add(trigger)
                }
            }
        }
    }
}