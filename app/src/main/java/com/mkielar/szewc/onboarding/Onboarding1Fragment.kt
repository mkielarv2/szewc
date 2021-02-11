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
import com.mkielar.szewc.databinding.FragmentOnboarding1Binding
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class Onboarding1Fragment : Fragment() {
    private lateinit var binding: FragmentOnboarding1Binding
    private var job: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnboarding1Binding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val vertical = MutableList(12) { Line(null) }
        val horizontal = MutableList(12) { Line(null) }
        val cells = MutableList(9) { Cell(null) }

        val gridView = GridView(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER
            )
            drawGrid(Grid(vertical, horizontal, cells, 3))
        }

        binding.container.addView(gridView)

        val p1 = Player("Player1", resources.getColor(R.color.color1))
        val p2 = Player("Player2", resources.getColor(R.color.color2))

        val animSpeed = 350L

        binding.nextButton.setOnClickListener {
            job?.cancel()
            findNavController().navigate(
                Onboarding1FragmentDirections.actionOnboarding1FragmentToOnboarding2Fragment()
            )
        }

        job = viewLifecycleOwner.lifecycleScope.launch {
            delay(animSpeed)
            while (true) {
                vertical.forEach { it.owner = null }
                horizontal.forEach { it.owner = null }
                cells.forEach { it.owner = null }

                gridView.drawGrid(Grid(vertical, horizontal, cells, 3))
                delay(animSpeed)

                vertical[4].owner = p1
                gridView.drawGrid(Grid(vertical, horizontal, cells, 3))
                delay(animSpeed)

                horizontal[1].owner = p2
                gridView.drawGrid(Grid(vertical, horizontal, cells, 3))
                delay(animSpeed)

                horizontal[10].owner = p1
                gridView.drawGrid(Grid(vertical, horizontal, cells, 3))
                delay(animSpeed)

                horizontal[7].owner = p2
                gridView.drawGrid(Grid(vertical, horizontal, cells, 3))
                delay(animSpeed)

                vertical[1].owner = p1
                gridView.drawGrid(Grid(vertical, horizontal, cells, 3))
                delay(animSpeed)

                horizontal[11].owner = p2
                gridView.drawGrid(Grid(vertical, horizontal, cells, 3))
                delay(animSpeed)

                horizontal[2].owner = p1
                gridView.drawGrid(Grid(vertical, horizontal, cells, 3))
                delay(animSpeed)

                vertical[6].owner = p2
                gridView.drawGrid(Grid(vertical, horizontal, cells, 3))
                delay(animSpeed)
            }
        }
    }
}