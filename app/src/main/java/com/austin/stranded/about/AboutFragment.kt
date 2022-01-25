package com.austin.stranded.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.austin.stranded.R
import com.austin.stranded.databinding.FragmentAboutBinding

/**
 * Simple fragment with some text talking about the development of this app and what it's
 * purpose is. Also has my email and the github link for the code.
 */
class AboutFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = DataBindingUtil.inflate<FragmentAboutBinding>(
            inflater,
            R.layout.fragment_about,
            container,
            false
        )


        /**
         * The "githubLink" button Opens the github page for the project.
         */
        binding.githubLink.setOnClickListener {
            val gitHubIntent = Intent( // creating the intent
                Intent.ACTION_VIEW,
                Uri.parse("https://github.com/Quintarous/stranded")
            )

            startActivity(gitHubIntent) // sending it to the android system
        }

        return binding.root
    }
}