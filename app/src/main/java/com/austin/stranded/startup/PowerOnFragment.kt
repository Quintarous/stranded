package com.austin.stranded.startup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.austin.stranded.database.UserSave
import com.austin.stranded.databinding.FragmentPowerOnBinding
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

        /**
         * Housekeeping
         */
        val binding = FragmentPowerOnBinding.inflate(inflater, container, false)

        // shares a view model with NoPowerFragment
        val viewModel: StartupViewModel by activityViewModels()

        var userSave: UserSave? = null


        // getting the UserSave from the database on fragment startup
        viewLifecycleOwner.lifecycleScope.launch {
            userSave = viewModel.getUserSave()
        }


        /**
         * When the powerOnButton is pressed, navigate to the ChatPageFragment so we can start
         * the next sequence.
         *
         * The argument passed into the navigation action is the number of the current sequence
         * according to the UserSave. This allows the ChatPageFragment to verify that the user
         * navigated from the PowerOnFragment. Thus getting the satisfaction of pressing the
         * button. Aside from this argument the ChatPageFragment has no way of knowing whether they
         * got to press the power on button or not. Since all that fragment can see is what's
         * in the UserSave.
         */
        binding.powerOnButton.setOnClickListener {
            if (userSave != null) { // if the userSave was successfully loaded from the database

                /**
                 * If for whatever reason we can't get the navigation action or the navigation
                 * itself fails. Show a toast with the exception so the user can bug report it.
                 */
                try {
                    val action = PowerOnFragmentDirections
                        .actionPowerOnFragmentToChatPageFragment(userSave!!.sequence)

                    findNavController().navigate(action) // then navigate with the argument passed in

                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        "Navigation failed with exception: $e",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } else { // if the UserSave is null, throw an exception
                throw Exception("PowerOnFragment: userSave was null when user clicked powerOnButton")
            }
        }

        return binding.root
    }
}