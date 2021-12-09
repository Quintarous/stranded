package com.example.stranded.startup

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.collectAsState
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.stranded.PowerOnBroadcastReceiver
import com.example.stranded.R
import com.example.stranded.database.UserSave
import com.example.stranded.databinding.FragmentNoPowerBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.random.Random

@AndroidEntryPoint
class NoPowerFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNoPowerBinding.inflate(inflater, container, false)

        val viewModel: StartupViewModel by activityViewModels()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userSaveFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .collect { userSave ->
                    if (userSave != null) {
                        if (userSave.isPowered) {
                            findNavController().navigate(R.id.action_noPowerFragment_to_powerOnFragment)
                        }

                        val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                        val intent = Intent(context, PowerOnBroadcastReceiver::class.java) // get notification
                        val existingPendingIntent = PendingIntent.getBroadcast(context, 1, intent,
                            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)

                        if (userSave.demoMode) { // if demo mode is turned on
                            binding.timeSkipButton.visibility = View.VISIBLE // make skip time button visible

                            if (existingPendingIntent != null) { // cancel notification
                                alarmManager.cancel(existingPendingIntent)
                                existingPendingIntent.cancel()
                            }

                        } else { // if demo mode is turned off
                            binding.timeSkipButton.visibility = View.GONE // make skip time button invisible

                            if (existingPendingIntent == null) { // if notification is not scheduled
                                val newPendingIntent = PendingIntent.getBroadcast(context, 1, intent,
                                    PendingIntent.FLAG_IMMUTABLE)

                                val calendar = Calendar.getInstance().apply {
                                    add(Calendar.HOUR_OF_DAY, Random.nextInt(4, 10))
                                }

                                // schedule a new notification
                                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, newPendingIntent)
                            }
                        }
                    }
                }
        }

// TODO test all the notification stuff with log statements
        // if the user save is updated while the user is sitting on this screen
        // check if isPowered = true and navigate to powerOn if so
        /*
        viewModel.userSaveFlow.observe(viewLifecycleOwner, { userSave ->

            Log.i("bruh" ,"NoPowerFragment: userSave = $userSave")

            if (userSave.isPowered) {
                findNavController().navigate(R.id.action_noPowerFragment_to_powerOnFragment)
            }

            val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val intent = Intent(context, PowerOnBroadcastReceiver::class.java) // get notification
            val existingPendingIntent = PendingIntent.getBroadcast(context, 1, intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)

            if (userSave.demoMode) { // if demo mode is turned on
                binding.timeSkipButton.visibility = View.VISIBLE // make skip time button visible

                if (existingPendingIntent != null) { // cancel notification
                    alarmManager.cancel(existingPendingIntent)
                    existingPendingIntent.cancel()
                }

            } else { // if demo mode is turned off
                binding.timeSkipButton.visibility = View.GONE // make skip time button invisible

                if (existingPendingIntent == null) { // if notification is not scheduled
                    val newPendingIntent = PendingIntent.getBroadcast(context, 1, intent,
                        PendingIntent.FLAG_IMMUTABLE)

                    val calendar = Calendar.getInstance().apply {
                        add(Calendar.HOUR_OF_DAY, Random.nextInt(4, 10))
                    }

                    // schedule a new notification
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, newPendingIntent)
                }
            }
        })
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