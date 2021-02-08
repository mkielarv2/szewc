package com.mkielar.szewc.onboarding

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.mkielar.szewc.R
import com.mkielar.szewc.core.model.Cell
import com.mkielar.szewc.core.model.Grid
import com.mkielar.szewc.core.model.Line
import com.mkielar.szewc.core.model.Player
import com.mkielar.szewc.core.view.GridView
import com.mkielar.szewc.databinding.FragmentOnboarding3Binding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Onboarding3Fragment : Fragment() {
    private lateinit var binding: FragmentOnboarding3Binding
    private var job: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnboarding3Binding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val vertical = MutableList(12) { Line(null) }
        val horizontal = MutableList(12) { Line(null) }
        val cells = MutableList(9) { Cell(null) }

        val p1 = Player("Player1", resources.getColor(R.color.color1))
        val p2 = Player("Player2", resources.getColor(R.color.color2))

        fillBaseVertical(vertical, p1, p2)
        fillBaseHorizontal(horizontal, p1, p2)
        fillBaseCell(cells, p1, p2)

        val gridView = GridView(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER
            )
            drawGrid(Grid(vertical, horizontal, cells, 3))
        }

        binding.container.addView(gridView)

        val animSpeed = 350L

        binding.nextButton.setOnClickListener {
            job?.cancel()
            findNavController().navigate(
                Onboarding3FragmentDirections.actionOnboarding3FragmentToMainFragment()
            )
        }

        job = viewLifecycleOwner.lifecycleScope.launch {
            delay(animSpeed)
            while (true) {
                fillBaseVertical(vertical, p1, p2)
                fillBaseHorizontal(horizontal, p1, p2)
                fillBaseCell(cells, p1, p2)

                gridView.drawGrid(Grid(vertical, horizontal, cells, 3))
                delay(animSpeed)

                vertical[5].owner = p2
                cells[2].owner = p2
                gridView.drawGrid(Grid(vertical, horizontal, cells, 3))
                delay(animSpeed)

                horizontal[7].owner = p2
                cells[5].owner = p2
                gridView.drawGrid(Grid(vertical, horizontal, cells, 3))
                delay(animSpeed)

                vertical[7].owner = p2
                cells[4].owner = p2
                gridView.drawGrid(Grid(vertical, horizontal, cells, 3))
                delay(animSpeed)

                horizontal[8].owner = p2
                cells[7].owner = p2
                gridView.drawGrid(Grid(vertical, horizontal, cells, 3))
                delay(animSpeed)

                vertical[11].owner = p2
                cells[8].owner = p2
                gridView.drawGrid(Grid(vertical, horizontal, cells, 3))
                delay(animSpeed)
            }
        }
    }

    private fun fillBaseVertical(vertical: MutableList<Line>, p1: Player, p2: Player) {
        vertical[0].owner = p1
        vertical[1].owner = p1
        vertical[2].owner = p1
        vertical[3].owner = p2
        vertical[4].owner = p1
        vertical[5].owner = null
        vertical[6].owner = p2
        vertical[7].owner = null
        vertical[8].owner = p2
        vertical[9].owner = p2
        vertical[10].owner = p1
        vertical[11].owner = null
    }

    private fun fillBaseHorizontal(horizontal: MutableList<Line>, p1: Player, p2: Player) {
        horizontal[0].owner = p2
        horizontal[1].owner = p2
        horizontal[2].owner = p1
        horizontal[3].owner = p1
        horizontal[4].owner = p1
        horizontal[5].owner = p2
        horizontal[6].owner = p2
        horizontal[7].owner = null
        horizontal[8].owner = null
        horizontal[9].owner = p1
        horizontal[10].owner = p2
        horizontal[11].owner = p1
    }

    private fun fillBaseCell(cells: MutableList<Cell>, p1: Player, p2: Player) {
        cells[0].owner = p1
        cells[1].owner = p1
        cells[2].owner = null
        cells[3].owner = p2
        cells[4].owner = null
        cells[5].owner = null
        cells[6].owner = p2
        cells[7].owner = null
        cells[8].owner = null
    }
}