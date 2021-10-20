package com.example.stranded.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.stranded.R
import com.example.stranded.chatpage.ChatPageViewModel
import com.example.stranded.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import java.lang.NumberFormatException

@AndroidEntryPoint
class SettingsFragment: Fragment() {

    // TODO add a setting for customizing the custom text view speed
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

        binding.letterDurationEditText.addTextChangedListener { text ->

            if (text.toString() != "") {

                val convertedInt: Int = try {
                    text.toString().toInt()
                }
                catch(e: NumberFormatException) {
                    69
                }

                viewModel.setLetterDuration(convertedInt)
            }

        }

        return binding.root
    }
}