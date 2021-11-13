package com.example.stranded.startup

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.stranded.PowerOnBroadcastReceiver
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

// TODO test if this navigation actually works when isPowered get changed to true
// if the user save is updated while the user is sitting on this screen
// check if isPowered = true and navigate to powerOn if so
        viewModel.userSave.observe(viewLifecycleOwner, { userSave ->
            if (userSave.isPowered) {
                findNavController().navigate(R.id.action_noPowerFragment_to_powerOnFragment)
            }

            if (userSave.demoMode) { // making the skip time button visible if user is in demo mode
                binding.timeSkipButton.visibility = View.VISIBLE
            } else {
                binding.timeSkipButton.visibility = View.GONE
            }
        })

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