package com.example.stranded.chatpage

import android.annotation.SuppressLint
import android.graphics.drawable.AnimationDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stranded.R
import com.example.stranded.databinding.FragmentChatPageBinding
import com.example.stranded.onAnimationFinished
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

@AndroidEntryPoint
class ChatPageFragment: Fragment() {

    private val viewModel: ChatPageViewModel by viewModels()
    private var mediaPlayer: MediaPlayer? = null

    private lateinit var gMeter: ImageView
    private lateinit var gMeterAnimation: AnimationDrawable

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //startup navigation logic
        viewModel.userSave.observe(viewLifecycleOwner, Observer { userSave ->
            if (userSave != null) {
                if (userSave.isPowered) {
                    if (userSave.line == 0)
                        findNavController()
                            .navigate(R.id.action_chatPageFragment_to_nav_graph_power_on)
                }
                else
                    findNavController()
                        .navigate(R.id.action_chatPageFragment_to_nav_graph_no_power)
            }
        })

        //data binding boilerplate code
        val binding = DataBindingUtil.inflate<FragmentChatPageBinding>(
            inflater,
            R.layout.fragment_chat_page,
            container,
            false
        )

        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        //making the gMeter available throughout the class
        gMeter = binding.gMeter

        //setting up the chat recycler adapter
        binding.chatRecycler.layoutManager = LinearLayoutManager(this.context).also {
            it.stackFromEnd = true
        }

        val chatRecyclerAdapter = ChatRecyclerAdapter(mutableListOf())
        binding.chatRecycler.adapter = chatRecyclerAdapter

        //adding a custom OnItemTouchListener to the chat recycler view
        //this custom listener overrides the onInterceptTouchEvent method to handle user "clicks"
        //it only acts when the motion event action is ACTION_DOWN or ACTION_UP
        var lastDownTouchx: Float? = null
        var lastDownTouchy: Float? = null
        binding.chatRecycler.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(recyclerView: RecyclerView, motionEvent: MotionEvent): Boolean {
                //checking if the event is a "finger down" and storing it's location if it was one
                if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                    lastDownTouchx = motionEvent.x
                    lastDownTouchy = motionEvent.y
                }

                //if the event is "finger up" then get the difference between the locations of the
                //original finger down and this finger up then only take action if they're close together
                //this is done to avoid taking action on "swipes" and other gestures
                else if (motionEvent.action == MotionEvent.ACTION_UP && lastDownTouchx != null && lastDownTouchy != null) {
                    val xDelta = abs(lastDownTouchx!! - motionEvent.x)
                    val yDelta = abs(lastDownTouchy!! - motionEvent.y)

                    if (xDelta < 30 && yDelta < 30) {
                        //telling the viewModel that the user tapped on this recycler view
                        viewModel.userTouch()
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

        val consoleRecyclerAdapter = ConsoleRecyclerAdapter(mutableListOf())
        binding.consoleRecycler.adapter = consoleRecyclerAdapter

        //viewModel observers go here
        //chat recyclers' dataset is updated through here
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

        //same as chat recycler dataset above^^^
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

        //observers for starting and stopping sound effects
        viewModel.stopSound.observe(viewLifecycleOwner, { stopSound() })

        viewModel.startSound.observe(viewLifecycleOwner, { trigger ->
            startSound(trigger.resourceId!!, trigger.loop!!)
        })

        viewModel.startSoundOneAndDone.observe(viewLifecycleOwner, { trigger ->
            startSoundOneAndDone(trigger.resourceId!!)
        })

        //observers for starting and stopping animations
        viewModel.stopAnim.observe(viewLifecycleOwner, { stopAnim() })

        viewModel.startAnim.observe(viewLifecycleOwner, { trigger ->
            startAnim(trigger.resourceId!!, trigger.loop!!)
        })

        viewModel.startAnimOneAndDone.observe(viewLifecycleOwner, { trigger ->
            startAnimOneAndDone(trigger.resourceId!!)
        })

        setHasOptionsMenu(true)

        return binding.root
    }

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
    private fun startSoundOneAndDone(sound: Int) {
        val lastSoundTrigger = viewModel.startSound.value!!

        stopSound()
        startMediaPlayback(sound, false)

        mediaPlayer?.setOnCompletionListener {
            startMediaPlayback(lastSoundTrigger.resourceId!!, lastSoundTrigger.loop!!)
        }
    }

    //methods for starting and stopping animations
    private fun stopAnim() {
        gMeter.setBackgroundResource(R.drawable.g_idle)
    }

    private fun startAnim(animation: Int, isLooping: Boolean) {
        startAnimation(animation, isLooping)
    }

    //meant for interrupting looping animations with one animation that plays one time then goes
    //back to the original loop it was on before
    private fun startAnimOneAndDone(animation: Int) {
        val lastAnimTrigger = viewModel.startAnim.value

        startAnimation(animation, false)

        gMeterAnimation.onAnimationFinished {

            if (lastAnimTrigger != null){
                startAnimation(lastAnimTrigger.resourceId!!, lastAnimTrigger.loop!!)
            } else { stopAnim() }
        }
    }

    override fun onStop() {
        if (mediaPlayer != null) {
            mediaPlayer?.release()
            mediaPlayer = null
        }
        super.onStop()
    }

    //functions for starting new sound effects and animations
    private fun startMediaPlayback(resource: Int, loop: Boolean) {
        mediaPlayer = MediaPlayer.create(context, resource)
        mediaPlayer?.isLooping = loop
        mediaPlayer?.start()
    }

    private fun startAnimation(animation: Int, isLooping: Boolean) {
        gMeter.setBackgroundResource(animation)
        gMeterAnimation = gMeter.background as AnimationDrawable
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