package com.austin.stranded.settings

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.austin.stranded.R
import com.austin.stranded.chatpage.ChatPageViewModel
import com.austin.stranded.databinding.FragmentSettingsBinding
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

        /**
         * Android housekeeping
         */
        val binding = DataBindingUtil.inflate<FragmentSettingsBinding>(
            inflater,
            R.layout.fragment_settings,
            container,
            false
        )

        val viewModel: ChatPageViewModel by activityViewModels() // sharing the ChatPageViewModel

        binding.lifecycleOwner = viewLifecycleOwner

        binding.viewModel = viewModel


        // setting the value of the demo mode switch to reflect the database value
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userSaveFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { userSave ->
                    if (userSave != null) {
                        binding.demoModeSwitch.isChecked = userSave.demoMode
                    }
                }
        }


        // telling the viewModel to save the new letterDuration value when the user changes it
        binding.letterDurationEditText.addTextChangedListener { text ->

            if (text.toString() != "") { // if the user actually put in a value

                // try to convert the string to an integer if it fails default to 70
                val convertedInt: Int = try {
                    text.toString().toInt()
                }
                catch(e: NumberFormatException) {
                    Toast.makeText(requireContext(), "Enter a valid number", Toast.LENGTH_SHORT).show()
                    70
                }

                viewModel.setLetterDuration(convertedInt) // if successful save and apply the new value
            }

        }


        // when the user toggles the demo mode switch save and apply the change
        binding.demoModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDemoMode(isChecked)
        }


        /**
         * The reset progress button opens a confirmation dialog asking if the user is sure they
         * want to reset all their progress. If they do then the resetProgress() method in the
         * ChatPageViewModel is called.
         */
        binding.resetButton.setOnClickListener {
            ResetProgressDialog(viewModel).show(this.parentFragmentManager, "reset progress dialog")
        }

        return binding.root
    }


    /**
     * Class for showing a confirmation dialog before the user resets their save progress.
     */
    class ResetProgressDialog(val viewModel: ChatPageViewModel) : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return activity?.let {
                val builder = AlertDialog.Builder(it)

                builder.setMessage(R.string.reset_progress_dialog)
                    .setPositiveButton(R.string.yes) { _, _ ->
                        viewModel.resetProgress()
                    }

                    .setNegativeButton(R.string.no) { _, _ ->
                        // do nothing
                    }

                builder.create()
            } ?: throw IllegalStateException("Settings Reset Progress Button: Activity is null!")
        }
    }
}