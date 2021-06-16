package com.example.stranded.chatpage

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.stranded.R
import com.example.stranded.databinding.FragmentChatPageBinding

class ChatPageFragment: Fragment() {

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

        //giving the chat recycler it's adapter
        val chatRecyclerAdapter = ChatRecyclerAdapter(mutableListOf())
        binding.chatRecycler.adapter = chatRecyclerAdapter

        chatRecyclerAdapter.dataset.add("bruh")
        chatRecyclerAdapter.notifyDataSetChanged()

        return binding.root
    }
}