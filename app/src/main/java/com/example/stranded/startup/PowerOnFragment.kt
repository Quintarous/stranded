package com.example.stranded.startup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.stranded.R
import com.example.stranded.database.UserSave
import com.example.stranded.databinding.FragmentPowerOnBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.Exception

@AndroidEntryPoint
class PowerOnFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPowerOnBinding.inflate(inflater, container, false)

        val viewModel: StartupViewModel by activityViewModels()
        var userSave: UserSave? = null

        viewLifecycleOwner.lifecycleScope.launch { // getting the UserSave from the db on fragment startup
            userSave = viewModel.getUserSave()
        }

        binding.powerOnButton.setOnClickListener {
            if (userSave != null) { // if the userSave was successfully loaded from the database
                val action = PowerOnFragmentDirections
                    .actionPowerOnFragmentToChatPageFragment(userSave!!.sequence)

                findNavController().navigate(action) // then navigate with the argument passed in
            } else { // else throw an exception
                throw Exception("PowerOnFragment: userSave was null when user clicked powerOnButton")
            }
        }

        return binding.root
    }
}