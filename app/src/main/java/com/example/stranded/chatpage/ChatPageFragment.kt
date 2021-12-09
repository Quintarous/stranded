package com.example.stranded.chatpage

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.icu.util.Calendar
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stranded.CustomTextView
import com.example.stranded.PowerOnBroadcastReceiver
import com.example.stranded.R
import com.example.stranded.databinding.FragmentChatPageNewBinding
import com.example.stranded.onAnimationFinished
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.random.Random
import com.example.stranded.chatpage.ChatPageViewModel.Event
import com.example.stranded.database.Trigger
import kotlinx.coroutines.flow.*

@AndroidEntryPoint
class ChatPageFragment: Fragment() {

    val args: ChatPageFragmentArgs by navArgs()

    private val viewModel: ChatPageViewModel by activityViewModels()
    private var mediaPlayer: MediaPlayer? = null

    private lateinit var gMeter: ImageView
    private lateinit var gMeterAnimation: AnimationDrawable

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //data binding boilerplate code
        val binding = DataBindingUtil.inflate<FragmentChatPageNewBinding>(
            inflater,
            R.layout.fragment_chat_page_new,
            container,
            false
        )

        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        // making the gMeter available throughout the class
        gMeter = binding.gMeter

        lifecycleScope.launch { // observing one shot events from the ViewModel
            viewModel.eventFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { event ->
                    when (event) {
                        is Event.NavToPowerOn -> {
                            findNavController()
                                .navigate(R.id.action_chatPageFragment_to_nav_graph_power_on)
                        }

                        is Event.NavToNoPower -> {
                            stopAnim()
                            stopSound()
                            findNavController()
                                .navigate(R.id.action_chatPageFragment_to_nav_graph_no_power)
                        }

                        is Event.StopSound -> { stopSound() }

                        is Event.StartSound -> {
                            val resourceId = getResourceId(event.trigger.resourceId!!)
                            startSound(resourceId, event.trigger.loop)
                        }

                        is Event.StartSoundOneAndDone -> {
                            val resourceId = getResourceId(event.newTrigger.resourceId!!)
                            startSoundOneAndDone(event.oldTrigger, resourceId)
                        }

                        is Event.StopAnim -> { stopAnim() }

                        is Event.StartAnim -> {
                            val resourceId = getResourceId(event.trigger.resourceId!!)
                            startAnimation(resourceId, event.trigger.loop)
                        }

                        is Event.StartAnimOneAndDone -> {
                            val resourceId = getResourceId(event.newTrigger.resourceId!!)
                            startAnimOneAndDone(event.oldTrigger, resourceId)
                        }

                        else -> { // must be Event.ScheduleNotification
                            // getting the alarmManager instance
                            val alarmManager =
                                context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                            // generating the time for the alarm to go off
                            val calendar = Calendar.getInstance().apply {
                                add(Calendar.HOUR_OF_DAY, Random.nextInt(5, 13))
                            }

                            // getting the pending intent
                            val intent = Intent(context, PowerOnBroadcastReceiver::class.java)
                            val pendingIntent = PendingIntent
                                .getBroadcast(context, 1, intent, PendingIntent.FLAG_IMMUTABLE)

                            // scheduling the alarm
                            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                        }
                    }
                }
        }

        // running the startup navigation logic in the ViewModel
        viewModel.startupNavigationCheck(args.fromPowerOn)


        // setting up the chat recycler adapter
        binding.chatRecycler.layoutManager = LinearLayoutManager(this.context).also {
            it.stackFromEnd = true
        }

        val chatRecyclerAdapter = ChatRecyclerAdapter(mutableListOf(), viewModel)
        binding.chatRecycler.adapter = chatRecyclerAdapter

        // adding a custom OnItemTouchListener to the chat recycler view
        // this custom listener overrides the onInterceptTouchEvent method to handle user "clicks"
        // it only acts when the motion event action is ACTION_DOWN or ACTION_UP
        var lastDownTouchx: Float? = null
        var lastDownTouchy: Float? = null
        binding.chatRecycler.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(recyclerView: RecyclerView, motionEvent: MotionEvent): Boolean {
                //checking if the event is a "finger down" and storing it's location if it was one
                if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    lastDownTouchx = motionEvent.x
                    lastDownTouchy = motionEvent.y
                }

// if the event is "finger up" then get the difference between the locations of the
// original finger down and this finger up then only take action if they're close together
// this is done to avoid taking action on "swipes" and other gestures
                else if (motionEvent.action == MotionEvent.ACTION_UP && lastDownTouchx != null && lastDownTouchy != null) {
                    val xDelta = abs(lastDownTouchx!! - motionEvent.x)
                    val yDelta = abs(lastDownTouchy!! - motionEvent.y)

                    if (xDelta < 30 && yDelta < 30) {

// grabbing the CustomTextView from the most recent line item and handing it to the ViewModel
                        if (viewModel.lastLine.type != "console") {

                            val holder = binding.chatRecycler.findViewHolderForAdapterPosition(
                                    viewModel.chatDataset.value?.size?.minus(1) ?: 0
                                )

                            val textView: CustomTextView? = when (holder) {

                                is ChatRecyclerAdapter.ScriptLineViewHolder -> {
                                    holder.viewBinding.lineText
                                }

                                is ChatRecyclerAdapter.UserLineViewHolder -> {
                                    holder.viewBinding.lineText
                                }

                                else -> {
                                    null
                                }
                            }

                            viewModel.userTouch(textView)
                        } else {

                            val textView: CustomTextView?

                            val holder = binding.consoleRecycler.findViewHolderForAdapterPosition(
                                viewModel.consoleDataset.value?.size?.minus(1) ?: 0
                            )

                            textView = if (holder is ConsoleRecyclerAdapter.ConsoleLineViewHolder) {
                                holder.viewBinding.consoleLine
                            } else {
                                null
                            }

                            viewModel.userTouch(textView)
                        }

                        return true
                    }
                }
                //otherwise return false when we're not doing anything with the touch event
                return false
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
            override fun onTouchEvent(recyclerView: RecyclerView, motionEvent: MotionEvent) {}
        })

        //setting up the prompt recycler adapter
        val promptRecyclerAdapter = PromptRecyclerAdapter(mutableListOf(), viewModel)
        binding.promptRecycler.adapter = promptRecyclerAdapter

        //setting up the console recycler adapter
        binding.consoleRecycler.layoutManager = LinearLayoutManager(this.context).also {
            it.stackFromEnd = true
        }

        val consoleRecyclerAdapter = ConsoleRecyclerAdapter(mutableListOf(), viewModel)
        binding.consoleRecycler.adapter = consoleRecyclerAdapter

        // viewModel observers go here

        // chat recyclers' dataset is updated through here
        viewModel.chatDataset.observe(viewLifecycleOwner, { lineList ->
            val dataset = chatRecyclerAdapter.dataset

            if (lineList.isNotEmpty()) {

                //if adapter dataset is already populated just add the new value to save resources
                if (dataset.size == lineList.size - 1) {
                    dataset.add(lineList.last())
                    chatRecyclerAdapter.notifyItemInserted(dataset.size - 1)
                    binding.chatRecycler.smoothScrollToPosition(dataset.size - 1)
                }
                //else copy the whole list from the viewModel
                else {
                    dataset.addAll(lineList)
                    chatRecyclerAdapter.notifyDataSetChanged()
                    binding.chatRecycler.smoothScrollToPosition(dataset.size - 1)
                }
            }
        })

        // same as chat recycler dataset above^^^
        viewModel.consoleDataset.observe(viewLifecycleOwner, { stringList ->
            val dataset = consoleRecyclerAdapter.dataset

            if (stringList.isNotEmpty()) {

                if (dataset.size == stringList.size - 1) {
                    dataset.add(stringList.last())
                    consoleRecyclerAdapter.notifyItemInserted(dataset.size - 1)
                    binding.consoleRecycler.smoothScrollToPosition(dataset.size - 1)
                }
                else {
                    dataset.addAll(stringList)
                    consoleRecyclerAdapter.notifyDataSetChanged()
                    binding.consoleRecycler.smoothScrollToPosition(dataset.size - 1)
                }
            }
        })

        viewModel.promptDataset.observe(viewLifecycleOwner, { promptList ->
            promptRecyclerAdapter.dataset = promptList
            promptRecyclerAdapter.notifyDataSetChanged()
        })


// observers for starting and stopping sound effects
        /*
        viewModel.stopSound.observe(viewLifecycleOwner, { stopSound() })

        viewModel.startSound.observe(viewLifecycleOwner, { trigger ->
            if (trigger != null) {
                val resourceId = getResourceId(trigger.resourceId!!)
                startSound(resourceId, trigger.loop)
            }
        })

        viewModel.startSoundOneAndDone.observe(viewLifecycleOwner, { trigger ->
            if (trigger != null) {
                val resourceId = getResourceId(trigger.resourceId!!)
                startSoundOneAndDone(resourceId)
            }
        })

// observers for starting and stopping animations
        viewModel.stopAnim.observe(viewLifecycleOwner, { stopAnim() })

        viewModel.startAnim.observe(viewLifecycleOwner, { trigger ->
            if (trigger != null) {
                val resourceId = getResourceId(trigger.resourceId!!)
                startAnim(resourceId, trigger.loop)
            }
        })

        viewModel.startAnimOneAndDone.observe(viewLifecycleOwner, { trigger ->
            if (trigger != null) {
                val resourceId = getResourceId(trigger.resourceId!!)
                startAnimOneAndDone(resourceId)
            }
        })
        */
        setHasOptionsMenu(true)

        return binding.root
    }

// if the fragment is paused (ie: user goes home or closes their phone) but not destroyed
// (like when the user navigates to the settings screen) then the view model observers won't
// fire and thus no sound is played even when it should.

// so to fix this we're checking if there is a valid start sound trigger in the view model
// and playing it if so
    /*
    override fun onStart() {
        if (viewModel.startSound.value != null) {
            val trigger = viewModel.startSound.value!!

            if(trigger.resourceId != null) {
                val resourceId = getResourceId(trigger.resourceId)
                startMediaPlayback(resourceId, trigger.loop)
            }
        }
        super.onStart()
    }
    */

    //methods for starting and stopping sound effects
    private fun stopSound() {

        if (mediaPlayer != null) {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer?.stop()
            }
        }
    }

    private fun startSound(sound: Int, isLooping: Boolean) {
        stopSound()
        startMediaPlayback(sound, isLooping)
    }

    //meant for interrupting looping sounds with one sound that plays one time
    //then goes back to the original loop it was on before
    private fun startSoundOneAndDone(lastSoundTrigger: Trigger, sound: Int) {
        stopSound()
        startMediaPlayback(sound, false)

        mediaPlayer?.setOnCompletionListener {
            val resourceId = getResourceId(lastSoundTrigger.resourceId!!)
            startMediaPlayback(resourceId, lastSoundTrigger.loop)
        }
    }

    //methods for starting and stopping animations
    private fun stopAnim() {
        gMeter.setBackgroundResource(R.drawable.g_idle)
    }

    //meant for interrupting looping animations with one animation that plays one time then goes
    //back to the original loop it was on before
    private fun startAnimOneAndDone(lastAnimTrigger: Trigger, animation: Int) {
        startAnimation(animation, false)

        viewLifecycleOwner.lifecycleScope.launch {
            gMeterAnimation.onAnimationFinished {
                Log.i("bruh" ,"onAnimationFinished")
                val resourceId = getResourceId(lastAnimTrigger.resourceId!!)
                startAnimation(resourceId, lastAnimTrigger.loop)
            }
        }
    }

    override fun onStop() {
        if (mediaPlayer != null) {
            mediaPlayer?.release()
            mediaPlayer = null
        }
        super.onStop()
    }

// functions for starting new sound effects and animations
    private fun startMediaPlayback(resource: Int, loop: Boolean) {
        mediaPlayer = MediaPlayer.create(context, resource)
        mediaPlayer?.isLooping = loop
        mediaPlayer?.start()
    }

    private fun startAnimation(animation: Int, isLooping: Boolean) {
        gMeter.apply {
            setBackgroundResource(animation)
            gMeterAnimation = background as AnimationDrawable
        }

        gMeterAnimation.isOneShot = !isLooping

        gMeterAnimation.start()
    }

    /*
    options menu used for adding test buttons delete if not needed

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.testing_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.initialize_database -> {
                viewModel.initializeDatabase()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
    */
}