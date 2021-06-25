package com.example.stranded.startup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.stranded.databinding.FragmentNoPowerBinding

class NoPowerFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNoPowerBinding.inflate(inflater, container, false)

        return binding.root
    }
}