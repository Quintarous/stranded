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
// TODO read and comment all code
// TODO mix the volume of all the sound effects
// TODO clean up all the livedata references that were replaced by eventChannel
// TODO add the github link to the about page when code is final
@HiltViewModel
class ChatPageViewModel @Inject constructor (private val repository: Repository): ViewModel() {

    /**
     * Variables to hold cached data
     */
    // Initialized with the view model, the sequence variable always holds the current sequence being run
    lateinit var sequence: Sequence

    // similarly these two variables hold all the triggers for the current sequence
    private val scriptTriggers: MutableList<Trigger> = mutableListOf()
    private val  promptTriggers: MutableList<Trigger> = mutableListOf()

    // promptResults caches the retrieved database results from the PromptResult db table
    // prompt result data is used to restore the user to their last saved position
    private lateinit var promptResults: List<Int>

    // Every time a line (of any type) is displayed it is cached here
    // This is essentially the "last line displayed"
    lateinit var lastLine: ScriptLine

    // the ChatRecyclerAdapter and ConsoleRecyclerAdapter both observe these LiveData
    // to tell if their last item has already been animated
    val chatLastItemAnimated = MutableLiveData<Int>()
    val consoleLastItemAnimated = MutableLiveData<Int>()

    // similarly to lastLine every fired trigger is cached by this ViewModel
    private var lastAnimTrigger: Trigger? = null
    private var lastSoundTrigger: Trigger? = null


    /**
     * LiveData that is observed by external components. All of the RecyclerViewAdapter datasets
     * are observed by the ChatPageFragment which passes on any updates to the adapters themselves
     */
    // dataset for the ChatRecyclerAdapter
    private val _chatDataset = MutableLiveData(mutableListOf<ScriptLine>())
    val chatDataset: LiveData<MutableList<ScriptLine>>
        get() = _chatDataset

    // dataset for the ConsoleRecyclerAdapter
    private val _consoleDataset = MutableLiveData<MutableList<String>>(mutableListOf())
    val consoleDataset: LiveData<MutableList<String>>
        get() = _consoleDataset

    // dataset for the PromptRecyclerAdapter
    private val _promptDataset = MutableLiveData<MutableList<PromptLine>>(mutableListOf())
    val promptDataset: LiveData<MutableList<PromptLine>>
        get() = _promptDataset

    // this letterDuration LiveData is observed by every CustomTextView in the ChatRecycler
    // and ConsoleRecycler. CustomTextView uses this integer to determine how long to wait
    // before displaying the next character in it's text.
    private val _letterDuration = MutableLiveData<Int>()
    val letterDuration: LiveData<Int>
        get() = _letterDuration

    // a StateFlow of the current UserSave from the database
    private val _userSaveFlow = MutableStateFlow<UserSave?>(null)
    val userSaveFlow: StateFlow<UserSave?> = _userSaveFlow

    // the MediaPlayer is the android component used to play sound effects (among other things
    // but we're just using it for sound). It's stored here in the ViewModel and is paused, resumed
    // and released by the MainActivity
    var mediaPlayer: MediaPlayer? = null


    /**
     * Every time this ViewModel is initialized we need to retrieve all the data necessary to
     * run whatever sequence the user is currently on. Which is a lot!
     */
    init {
        // grabbing the sequence, letterDuration and prompt results from the repository
        viewModelScope.launch {
            //TODO remove this delay when finalized
            delay(1000)
            collectUserSave() // getting the UserSave StateFlow flowing

            // grabbing the up to date UserSave data for our own use
            val userSave = repository.getUserSave()

            // grabbing the sequence we need to run from the database
            sequence = repository.getSequence(userSave.sequence)

            // dumping the current letterDuration value from the userSave into our LiveData
            _letterDuration.value = userSave.letterDuration

            // getting the promptResults from the database
            promptResults = repository.getPromptResults().map { it.result }

            // sorting this sequences triggers into their respective lists
            for (trigger in sequence.triggers) {
                when (trigger.triggerType) {
                    "script" -> scriptTriggers.add(trigger) // if it's a script trigger
                    else -> promptTriggers.add(trigger) // else it must be a prompt trigger
                }
            }

            // finally calling the complex and recursive restoreSave() method to progress the app
            // up to where the user last left off
            restoreSave(0, "script", 0, userSave)
        }
    }


    /**
     * Collecting the userSaveFlow from the repository and emitting values to _userSaveFlow.
     */
    private fun collectUserSave() {
        viewModelScope.launch {
            repository.userSaveFlow.stateIn(viewModelScope).collect{ userSave ->
                _userSaveFlow.emit(userSave)
            }
        }
    }


    /**
     * Holder class for the one shot events that are sent to the ChatPageFragment to tell it to do stuff.
     */
    sealed class Event {
        object NavToNoPower: Event() // navigates to the NoPowerFragment
        object NavToPowerOn: Event() // navigates to the PowerOnFragment
        object NavToEnding: Event() // navigates to the EndingFragment
        object ScheduleNotification: Event() // schedules the one and only notification

        object StopSound: Event() // stops any currently playing sound effects
        // plays the sound effect contained in the trigger
        data class StartSound(val trigger: Trigger): Event()
        // assumes oldTrigger is a looping sound effect and plays the newTrigger sound before
        // switching back to the old looping one
        data class StartSoundOneAndDone(val oldTrigger: Trigger, val newTrigger: Trigger): Event()

        // these are all the same as their sound trigger counterparts just for animations instead
        object StopAnim: Event()
        data class StartAnim(val trigger: Trigger): Event()
        data class StartAnimOneAndDone(val oldTrigger: Trigger, val newTrigger: Trigger): Event()
    }


    /**
     * eventFlow is a hot flow of Event objects observed by the ChatPageFragment. Because it is
     * using .receiveAsFlow() on a channel, every Event is effectively one shot. They will sit in
     * the que until the ChatPageFragment consumes them at which point they are removed.
     */
    private val eventChannel = Channel<Event>(2, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val eventFlow = eventChannel.receiveAsFlow()


    /**
     * startupNavigationCheck is called by the ChatPageFragment every time it is created. This is
     * done because ChatPageFragment is the home page of the app. And if the user has not started
     * a sequence yet then they need to be taken to either the PowerOnFragment or the NoPowerFragment
     * (depending on whether UserSave.isPowered is currently true or false).
     */
    fun startupNavigationCheck(fromPowerOn: Int) {
        viewModelScope.launch {
            val userSave = repository.getUserSave() // grabbing an up to date UserSave

            if (userSave.isPowered) { // if isPowered is true

                if (userSave.line == 0) { // then check if the sequence has not yet been started

                    // if it hasn't been started yet then check if the "Power On" button was pressed
                    if (fromPowerOn == userSave.sequence) {
                        startSequence() // now can we start the sequence

                    } else { // "Power On" button was NOT pressed
                        // navigate back to the PowerOnFragment user needs to hit "Power On" first
                        eventChannel.trySend(Event.NavToPowerOn)
                    }

                } else { // we are mid sequence
                    // assuming the last animation trigger isn't null fire it again
                    // this is mostly for looping animations since if the user navigates away
                    // then back we need to manually restart the animation
                    if (lastAnimTrigger != null) fireTrigger(lastAnimTrigger!!)
                }

            } else { // isPowered is false so navigate to the NoPowerFragment
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
                        fireTrigger(trigger)

                        /*
                        if (trigger.loop) fireTrigger(trigger)
                        else {
                            if (trigger.resourceId == null) fireTrigger(trigger)
                        }
                        */
                    }
                }

                lastLine = scriptLine // caching the last line displayed

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
                }
                else {
                    if (trigger.oneAndDone) {
                        if (lastAnimTrigger != null) {
                            eventChannel.trySend(Event.StartAnimOneAndDone(lastAnimTrigger!!, trigger))
                        } else {
                            throw Throwable("tried to send Event.StartAnimOneAndDone but lastAnimTrigger was null")
                        }
                    }
                    else {
                        eventChannel.trySend(Event.StartAnim(trigger))
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