package com.example.stranded.chatpage

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.stranded.R
import com.example.stranded.databinding.FragmentChatPageBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.Console

@AndroidEntryPoint
class ChatPageFragment: Fragment() {

    private lateinit var gMeterUpAnimation: AnimationDrawable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //data binding boilerplate code
        val binding = DataBindingUtil.inflate<FragmentChatPageBinding>(
            inflater,
            R.layout.fragment_chat_page,
            container,
            false
        )

        binding.lifecycleOwner = viewLifecycleOwner

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

        return binding.root
    }
}