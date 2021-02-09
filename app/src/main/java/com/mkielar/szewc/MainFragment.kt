package com.mkielar.szewc

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.mkielar.szewc.core.model.Player
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



        binding.onboardingButton.setOnClickListener {
            findNavController()
                .navigate(MainFragmentDirections.actionMainFragmentToOnboarding1Fragment())
        }

        binding.startButton.setOnClickListener {
            var valid = true

            val p1Nick = binding.player1EditText.text.toString()
            val p2Nick = binding.player2EditText.text.toString()

            if (!p1Nick.matches("[a-zA-Z0-9]{1,15}".toRegex())) {
                valid = false
                binding.player1EditText.error = "Niepoprawny nick"
            }

            if (!p2Nick.matches("[a-zA-Z0-9]{1,15}".toRegex())) {
                valid = false
                binding.player2EditText.error = "Niepoprawny nick"
            }

            if(p1Nick == p2Nick) {
                valid = false
                binding.player1EditText.error = "Nicki nie mogą być takie same"
                binding.player2EditText.error = "Nicki nie mogą być takie same"
            }

            val p1 = Player(p1Nick, resources.getColor(R.color.color1))
            val p2 = Player(p2Nick, resources.getColor(R.color.color2))

            if (valid) {
                findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToGameFragment(arrayOf(p1, p2))
                )
            }
        }
    }
}