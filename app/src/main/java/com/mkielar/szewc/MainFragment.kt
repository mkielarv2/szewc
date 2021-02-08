package com.mkielar.szewc

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mkielar.szewc.databinding.MainFragmentBinding

class MainFragment : Fragment() {
    private val firstStartKey = "first_start"

    private lateinit var binding: MainFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        if (sharedPreferences.getBoolean(firstStartKey, true)) {
            val edit = sharedPreferences.edit()
            edit.putBoolean(firstStartKey, false)
            edit.apply()

            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToOnboarding1Fragment()
            )
        }


        binding.startButton.setOnClickListener {
            findNavController()
                .navigate(MainFragmentDirections.actionMainFragmentToGameFragment())
        }

        binding.onboardingButton.setOnClickListener {
            findNavController()
                .navigate(MainFragmentDirections.actionMainFragmentToOnboarding1Fragment())
        }
    }
}