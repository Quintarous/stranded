package com.example.stranded.ending

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.stranded.R
import com.example.stranded.databinding.FragmentEndingBinding

class EndingFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<FragmentEndingBinding>(
                inflater,
                R.layout.fragment_ending,
                container,
                false
        )

        return binding.root
    }
}