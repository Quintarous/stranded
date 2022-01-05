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
// TODO be sure to explain in the readme how lines contain data for which line comes next internally
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

    /**
     * setLetterDuration() sets the letter duration value both here in the ViewModel (where
     * the actual CustomTextView views in the ui get their value from) and in the database
     * in the UserSave table so it's saved permanently.
     */
    fun setLetterDuration(value: Int) {

        _letterDuration.value = value // setting the new value here

        // updating the UserSave db table so their preference is saved
        viewModelScope.launch {
            val newUserSave = repository.getUserSave().apply {
                this.letterDuration = value
            }

            repository.updateUserSaveData(newUserSave)
        }
    }


    /**
     * updates the database with the new demoMode value
     */
    fun setDemoMode(value: Boolean) {

        viewModelScope.launch {
            val oldUserSave = repository.getUserSave()

            val newUserSave = oldUserSave.apply {
                demoMode = value
            }

            repository.updateUserSaveData(newUserSave)
        }
    }

    // "starts" the sequence by displaying the first line
    fun startSequence() = displayScriptLine(sequence.scriptLines[0])


    /**
     * restoreSave is a recursive method that progresses the app to whatever line the user was on
     * according the the UserSave line and lineType. It is called with a specific line and
     * determines whether that line is the stopping point, needs a trigger fired on it, or simply
     * needs to be displayed before moving on to the next.
     *
     * lineIndex: the index of a ScriptLine or prompt set.
     *
     * lineType: either "script" or "prompt" this determines whether the lineIndex is referring to a
     * ScriptLine or prompt set.
     *
     * promptResultIndex: used by restoreSave() to keep track of which prompt result
     * value it needs to look at when recreating the users choices. It starts at 0 and increments
     * every time a prompt result is chosen. Thus recreating the users dialogue options from the
     * first to the last.
     */
    private fun restoreSave(lineIndex: Int, lineType: String, promptResultIndex: Int, userSave: UserSave) {

        if (userSave.line == 0) return // if we're on line 0 then there's nothing to do so return

        when(lineType) { // different logic needs to be run if we're given a ScriptLine vs a prompt Set

            "script" -> { // if it's a ScriptLine
                val scriptLine = sequence.scriptLines[lineIndex] // grabbing the ScriptLine object

                for (trigger in scriptTriggers) {
                    /**
                     * So the triggerId goes off of a ScriptLines index + 1 as opposed to just the
                     * id of that unique ScriptLine. This was done because I had to manually create
                     * the entire database one entry at a time and being able to reference lines
                     * by count ie: line 1, line 69, line 72 etc... Was WAY easier than looking
                     * up that lines individual id that room assigned to it.
                     */
                    // if the trigger is supposed to fire on this scriptLine
                    if (trigger.triggerId == sequence.scriptLines.indexOf(scriptLine) + 1) {
                        fireTrigger(trigger) // then fire it
                    }
                }

                lastLine = scriptLine // caching the last line displayed

                when (scriptLine.type) { // actually displaying the ScriptLine in the ui
                    "console" -> _consoleDataset.value!!.add(scriptLine.line)

                    else -> _chatDataset.value!!.add(scriptLine)
                }

                // if this script line is the stopping point as defined in the UserSave then return
                if (userSave.lineType == "script" && scriptLine.id == userSave.line) {
                    // telling the ui recycler views they've got new data to display
                    _chatDataset.notifyObserver()
                    _consoleDataset.notifyObserver()
                    _promptDataset.notifyObserver()
                    return
                }

                /**
                 * This is the recursive part. After displaying the line and knowing it's not the
                 * stopping point we gotta move on and do the same thing for the next line/prompt.
                 */
                restoreSave(
                    scriptLine.next - 1,
                    scriptLine.nextType,
                    promptResultIndex,
                    userSave
                )
                return
            }

            else -> { // if it's a prompt Set

                val set = sequence.sets[lineIndex] // grabbing the set object

                for (trigger in promptTriggers) { // same trigger logic as before!

                    // if the trigger is supposed to fire on this Set
                    if (trigger.triggerId == set.number) fireTrigger(trigger) // then fire it
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
                // picked! So we'll use the promptResults table to ascertain which PromptLine they chose.
                val promptLine = set.lines[promptResults[promptResultIndex]]

                // the _chatDataset only takes ScriptLines so we'll need to generate one in order to
                // display the text from our promptLine
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
                // and the response are not the stopping point. So we increment the
                // promptResultIndex and keep going!
                restoreSave(
                    promptLine.next - 1, promptLine.nextType,
                    promptResultIndex + 1,
                    userSave
                )
                return
            }
        }
    }


    /**
     * Called by the block of code in the ChatPageFragment that intercepts user touch events.
     *
     * userTouch is called with the most recent CustomTextView to be displayed. If said
     * CustomTextView is still animating it will simply skip the animation and return.
     *
     * Otherwise it Displays the next line/prompt set when the user taps on the chatRecyclerView.
     * Unless the lastLine.nextType == "end" in which case it will end the sequence, reset
     * the UserSave to default (thus resetting the users progress so they can play the story again),
     * and go to the ending credits fragment.
     */
    fun userTouch(textView: CustomTextView?) {

        if (textView != null) {

            if (textView.getAnimationStatus()) { // skipping the animation if it's still running
                textView.skipAnimation()
                return
            }
        }

        when (lastLine.nextType) {
            "end" -> { // when the last line was the last one in the sequence

                viewModelScope.launch {
                    val currentUserSave = repository.getUserSave() // getting the UserSave

                    if (currentUserSave.sequence < 8) { // if the current sequence is not the last one

                        val newUserSave = currentUserSave.apply { // updating the user save
                            isPowered = false
                            sequence += 1
                            line = 0
                        }
                        repository.updateUserSaveData(newUserSave) // pushing the change to the db

                        prepForNextSequence() // clearing the viewModel for the next sequence

                        repository.clearPromptResult() // clearing the users saved choices

                        if (!newUserSave.demoMode) { // if demoMode is off
                            // tell the fragment to schedule notification + powerOn work
                            eventChannel.trySend(Event.ScheduleNotification)
                        }

                        eventChannel.trySend(Event.NavToNoPower)

                    } else { // if the current sequence is the last one (the user completed the game)

                        // reset the UserSave to defaults
                        val clearedUserSave = currentUserSave.apply {
                            isPowered = true
                            sequence = 1
                            line = 0
                            lineType = "script"
                        }

                        repository.updateUserSaveData(clearedUserSave) // push the new UserSave
                        prepForNextSequence() // clear the view model
                        repository.clearPromptResult() // clear the PromptResult db table

                        eventChannel.trySend(Event.NavToEnding) // go to the ending credits fragment
                    }
                }
            }

            // if the nextType is a ScriptLine then display it
            "script" -> displayScriptLine(sequence.scriptLines[lastLine.next - 1])

            // at this point it can only be a prompt set so display that
            else -> displayPromptSet(sequence.sets[lastLine.next - 1])
        }
    }


    /**
     * Called by the PromptRecyclerAdapter when a prompt button is tapped by the user.
     *
     * index: the index (within it's Set) of the PromptLine that was selected
     *
     * promptSelected() clears the adapters dataset to get the prompts off the screen, then
     * displays the prompt in the chatRecyclerView. As well as records the index of the
     * selected PromptLine in the PromptResult database table.
     */
    fun promptSelected(index: Int) {

        // grabbing the selected prompt from the adapters dataset
        val promptLine = promptDataset.value!![index]

        if (promptLine.nextType == "end") return // do nothing if this PromptLine ends the sequence

        _promptDataset.value!!.clear() // getting the prompt buttons off the screen
        _promptDataset.notifyObserver()

        // generating a ScriptLine object to display the users dialogue choice
        displayScriptLine(ScriptLine(
            0,
            0,
            "user",
            promptLine.line,
            promptLine.next,
            promptLine.nextType
        ))

        // updating the PromptResult database table with the users choice
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
        
        for (trigger in promptTriggers) { // if a trigger needs to be fired on this Set
            if (trigger.triggerId == set.number) fireTrigger(trigger) // then fire it
        }

        // updating the UserSave
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