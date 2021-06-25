package com.example.stranded.startup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.stranded.R
import com.example.stranded.databinding.FragmentPowerOnBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PowerOnFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPowerOnBinding.inflate(inflater, container, false)

        val viewModel: StartupViewModel by viewModels()

        binding.powerOnButton.setOnClickListener {
            viewModel.startSequence()
            findNavController().navigate(R.id.action_powerOnFragment_to_nav_graph)
        }

        return binding.root
    }
}