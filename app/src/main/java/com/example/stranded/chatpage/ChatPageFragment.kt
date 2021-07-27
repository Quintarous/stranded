package com.example.stranded.chatpage

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
import androidx.recyclerview.widget.RecyclerView
import com.example.stranded.R
import com.example.stranded.database.Trigger
import com.example.stranded.databinding.FragmentChatPageBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs

@AndroidEntryPoint
class ChatPageFragment: Fragment() {

    private val viewModel: ChatPageViewModel by viewModels()
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var gMeterAnimation: AnimationDrawable

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

        //setting up the chat recycler adapter
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
        val consoleRecyclerAdapter = ConsoleRecyclerAdapter(mutableListOf())
        binding.consoleRecycler.adapter = consoleRecyclerAdapter

        //viewModel observers go here
        //chat recyclers' dataset is updated through here
        viewModel.chatDataset.observe(viewLifecycleOwner, { lineList ->
            if (lineList.isNotEmpty()) {

                //if adapter dataset is already populated just add the new value to save resources
                if (chatRecyclerAdapter.dataset.size == lineList.size - 1) {
                    chatRecyclerAdapter.dataset.add(lineList.last())
                    chatRecyclerAdapter.notifyItemInserted(chatRecyclerAdapter.itemCount - 1)
                }
                //else copy the whole list from the viewModel
                else {
                    chatRecyclerAdapter.dataset.addAll(lineList)
                    chatRecyclerAdapter.notifyDataSetChanged()
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
                }
                else {
                    dataset.addAll(stringList)
                    consoleRecyclerAdapter.notifyDataSetChanged()
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
        viewModel.stopAnim.observe(viewLifecycleOwner, { stopAnim(binding.gMeter) })

        viewModel.startAnim.observe(viewLifecycleOwner, { trigger ->
            startAnim(binding.gMeter, trigger.resourceId!!, trigger.loop!!)
        })

        viewModel.startAnimOneAndDone.observe(viewLifecycleOwner, { trigger ->
            startAnimOneAndDone(binding.gMeter, trigger.resourceId!!)
        })

        setHasOptionsMenu(true)

        return binding.root
    }

    //methods for starting and stopping sound effects
    fun stopSound() {
        Log.i("bruh", "stopSound()")

        if (mediaPlayer != null) {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer?.stop()
            }
        }
    }

    fun startSound(sound: Int, isLooping: Boolean) {
        Log.i("bruh", "startSound($sound, $isLooping)")
        stopSound()
        startMediaPlayback(sound, isLooping)
    }

    fun startSoundOneAndDone(sound: Int) {
        Log.i("bruh", "startSoundOneAndDone($sound)")
        val lastSoundTrigger = viewModel.startSound.value!!

        stopSound()
        startMediaPlayback(sound, false)

        mediaPlayer?.setOnCompletionListener {
            startMediaPlayback(lastSoundTrigger.resourceId!!, lastSoundTrigger.loop!!)
        }
    }

    //methods for starting and stopping animations
    fun stopAnim(view: ImageView) {
        Log.i("bruh", "stopAnim()")
        view.setBackgroundResource(R.drawable.g_idle)
    }

    fun startAnim(view: ImageView, animation: Int, isLooping: Boolean) {
        Log.i("bruh", "startAnim()")
        startAnimation(view, animation, isLooping)
    }

    fun startAnimOneAndDone(view: ImageView, animation: Int) {
        Log.i("bruh", "startAnimOneAndDone()")
        val lastAnimTrigger = viewModel.startAnim.value

        //TODO queue the last animation when this one finishes
        startAnimation(view, animation, false)
    }

    override fun onStop() {
        Log.i("bruh", "onStop() called")
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

    private fun startAnimation(view: ImageView, animation: Int, isLooping: Boolean) {
        view.setBackgroundResource(animation)
        gMeterAnimation = view.background as AnimationDrawable
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