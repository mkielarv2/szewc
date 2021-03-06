package com.mkielar.szewc.core.view

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.FrameLayout.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout.LayoutParams.WRAP_CONTENT
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mkielar.szewc.core.viewmodel.GameViewModel
import com.mkielar.szewc.databinding.FragmentGameBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GameFragment : Fragment() {
    private val viewModel: GameViewModel by viewModels()
    val args: GameFragmentArgs by navArgs()

    private lateinit var fragmentGameBinding: FragmentGameBinding
    private lateinit var gridView: GridView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentGameBinding = FragmentGameBinding.inflate(inflater)
        return fragmentGameBinding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val players = args.players
        if (players.size < 2) {
            findNavController().popBackStack()
        }
        viewModel.players = players.toList()

        fragmentGameBinding.p1Nick.text = players[0].nick
        fragmentGameBinding.p2Nick.text = players[1].nick
        fragmentGameBinding.p1Score.text = "0"
        fragmentGameBinding.p2Score.text = "0"

        gridView = GridView(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, Gravity.CENTER)
            verticalCallback = {
                viewModel.verticalTouch(it)
            }
            horizontalCallback = {
                viewModel.horizontalTouch(it)
            }
        }

        fragmentGameBinding.container.addView(gridView)

        viewModel.scoreUpdateCallback = {
            fragmentGameBinding.p1Score.text = players[0].points.toString()
            fragmentGameBinding.p2Score.text = players[1].points.toString()
        }

        viewModel.gridUpdateCallback = {
            if ((it.turnCounter + it.offsetCounter) % it.players.size == 0) {
                fragmentGameBinding.p1Card.setCardBackgroundColor(it.players[0].color)
                fragmentGameBinding.p2Card.setCardBackgroundColor(Color.WHITE)
            } else {
                fragmentGameBinding.p2Card.setCardBackgroundColor(it.players[1].color)
                fragmentGameBinding.p1Card.setCardBackgroundColor(Color.WHITE)
            }
            gridView.drawGrid(it.grid)
        }

        viewModel.endGameCallback = {
            if (it.size == 1) {
                Toast.makeText(requireContext(), "Wygrał ${it.first().nick}", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(requireContext(), "Remis", Toast.LENGTH_SHORT).show()
            }

            viewLifecycleOwner.lifecycleScope.launch {
                delay(2000)
                findNavController().popBackStack()
            }
        }

        viewModel.startGame()
    }
}