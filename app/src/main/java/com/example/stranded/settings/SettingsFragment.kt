package com.example.stranded.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.example.stranded.R
import com.example.stranded.chatpage.ChatPageViewModel
import com.example.stranded.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.NumberFormatException

@AndroidEntryPoint
class SettingsFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentSettingsBinding>(
            inflater,
            R.layout.fragment_settings,
            container,
            false
        )

        val viewModel: ChatPageViewModel by activityViewModels()

        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userSaveFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { userSave ->
                    if (userSave != null) {
                        binding.demoModeSwitch.isChecked = userSave.demoMode
                    }
                }
        }


// telling the viewModel to deal with the new letterDuration value when the user changes it
        binding.letterDurationEditText.addTextChangedListener { text ->

            if (text.toString() != "") {

                val convertedInt: Int = try {
                    text.toString().toInt()
                }
                catch(e: NumberFormatException) {
                    throw Throwable("NumberFormatException in SettingsFragment")
                }

                viewModel.setLetterDuration(convertedInt)
            }

        }

        binding.demoModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDemoMode(isChecked)
        }

        return binding.root
    }
}