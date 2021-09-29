package com.example.stranded.startup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.stranded.R
import com.example.stranded.databinding.FragmentNoPowerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoPowerFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNoPowerBinding.inflate(inflater, container, false)

        val viewModel: StartupViewModel by viewModels()

        // if the user save is updated while the user is sitting on this screen
        // check if isPowered = true and navigate to powerOn if so
        viewModel.userSave.observe(viewLifecycleOwner, Observer { userSave ->
            if (userSave.isPowered) {
                findNavController().navigate(R.id.action_noPowerFragment_to_powerOnFragment)
            }
        })

        return binding.root
    }
}