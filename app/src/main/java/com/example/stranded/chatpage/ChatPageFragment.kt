package com.example.stranded.chatpage

import android.graphics.drawable.AnimationDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.stranded.R
import com.example.stranded.databinding.FragmentChatPageBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.Console

@AndroidEntryPoint
class ChatPageFragment: Fragment() {

    private val viewModel: ChatPageViewModel by viewModels()
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var gMeterUpAnimation: AnimationDrawable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //startup navigation logic
        viewModel.userSave.observe(viewLifecycleOwner, Observer { userSave ->
            if (userSave != null) {
                if (userSave.isPowered) {
                    if (userSave.line == 0) findNavController()
                        .navigate(R.id.action_chatPageFragment_to_nav_graph_power_on)
                }
                else findNavController().navigate(R.id.action_chatPageFragment_to_nav_graph_no_power)
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

        chatRecyclerAdapter.dataset.add("bruh")
        chatRecyclerAdapter.notifyDataSetChanged()

        chatRecyclerAdapter.dataset.add("hi")

        //setting up the prompt recycler adapter
        val promptRecyclerAdapter = PromptRecyclerAdapter(mutableListOf())
        binding.promptRecycler.adapter = promptRecyclerAdapter

        promptRecyclerAdapter.dataset.addAll(placeholderSet().lines)
        promptRecyclerAdapter.notifyDataSetChanged()

        //setting up the console recycler adapter
        val consoleRecyclerAdapter = ConsoleRecyclerAdapter(mutableListOf())
        binding.consoleRecycler.adapter = consoleRecyclerAdapter

        consoleRecyclerAdapter.dataset.addAll(listOf("<<< MESSAGE ONE >>>", "<<< MESSAGE TWO >>>"))
        consoleRecyclerAdapter.notifyDataSetChanged()

        //g meter animation
        binding.gMeter.apply {
            setBackgroundResource(R.drawable.g_meter_up_animation)
            gMeterUpAnimation = background as AnimationDrawable
        }

        binding.gMeter.setOnClickListener {
            if (gMeterUpAnimation.isRunning) gMeterUpAnimation.stop()
            else gMeterUpAnimation.start()
        }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onStop() {
        if (mediaPlayer != null) mediaPlayer?.release()
        super.onStop()
    }

    //functions for controlling the sound effects
    fun startMediaPlayback(resource: Int, loop: Boolean) {
        mediaPlayer = MediaPlayer.create(context, resource)
        mediaPlayer?.isLooping = loop
        mediaPlayer?.start()
    }

    fun stopMediaPlayback() = mediaPlayer?.stop()

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