package com.austin.stranded.startup

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.austin.stranded.PowerOnBroadcastReceiver
import com.austin.stranded.R
import com.austin.stranded.databinding.FragmentNoPowerBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.random.Random


/**
 * The NoPowerFragment is the screen the user will be stuck on in between sequences. (In the
 * narrative they are recharging their battery) It simply displays a text view saying they have
 * no power, and waits for the UserSave.isPowered to be set to true by the notification.
 *
 * Alternatively the user can go to settings and enable demo mode (if it's not already). This adds
 * a button to the NoPowerFragment that says "Skip The Wait!". When they press it the notification
 * is fired immediately so they can instantly progress.
 */
@AndroidEntryPoint
class NoPowerFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        /**
         * Android housekeeping
         */
        val binding = FragmentNoPowerBinding.inflate(inflater, container, false)

        // NoPowerFragment shares the same view model with PowerOnFragment
        val viewModel: StartupViewModel by activityViewModels()


        /**
         * This code runs once when the fragment boots up and again every time the UserSave changes.
         * We need to navigate to PowerOnFragment if userSave.isPowered == true. We need to make
         * sure if demo mode is on that the notification is canceled. And alternatively if demo mode
         * is off we need to ensure the notification is scheduled.
         */
        viewLifecycleOwner.lifecycleScope.launch {

            // collecting the UserSave flow
            viewModel.userSaveFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { userSave ->

                    if (userSave != null) { // if UserSave is not null

                        if (userSave.isPowered) { // check if it's powered
                            // navigate to the PowerOnFragment
                            findNavController().navigate(R.id.action_noPowerFragment_to_powerOnFragment)
                        }

                        /**
                         * Here we are retrieving the exact pending intent token of the notification.
                         * If it already exists it will be in existingPendingIntent. But if it
                         * doesn't exist then existingPendingIntent will be null.
                         */
                        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                        val intent = Intent(context, PowerOnBroadcastReceiver::class.java)

                        val existingPendingIntent = PendingIntent.getBroadcast(context, 1, intent,
                            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)

                        if (userSave.demoMode) { // if demo mode is turned on
                            binding.timeSkipButton.visibility = View.VISIBLE // make skip time button visible

                            // cancel the notification if it exists
                            if (existingPendingIntent != null) {
                                alarmManager.cancel(existingPendingIntent)
                                existingPendingIntent.cancel()
                            }

                        } else { // if demo mode is turned off
                            binding.timeSkipButton.visibility = View.GONE // make skip time button invisible

                            // if notification is not scheduled then schedule a new one
                            if (existingPendingIntent == null) {
                                val newPendingIntent = PendingIntent.getBroadcast(context, 1, intent,
                                    PendingIntent.FLAG_IMMUTABLE)

                                val calendar = Calendar.getInstance().apply {
                                    add(Calendar.HOUR_OF_DAY, Random.nextInt(4, 10))
                                }

                                // scheduling the new notification
                                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, newPendingIntent)
                            }
                        }
                    }
                }
        }


        /**
         * When the timeSkipButton is pressed, fire the notification immediately and show a toast.
         * When the notification is fired it sets the UserSave.isPowered to true.
         */
        binding.timeSkipButton.setOnClickListener { // firing the notification immediately and showing a toast

            val intent = Intent(context, PowerOnBroadcastReceiver::class.java)

            PendingIntent.getBroadcast(
                context,
                1,
                intent,
                PendingIntent.FLAG_IMMUTABLE
            ).send()

            Toast.makeText(context, "Notification Fired Early.", Toast.LENGTH_LONG).show()
        }

        return binding.root
    }
}