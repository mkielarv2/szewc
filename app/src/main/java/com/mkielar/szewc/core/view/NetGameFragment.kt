package com.mkielar.szewc.core.view

import android.app.ProgressDialog
import android.content.DialogInterface
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
import com.mkielar.szewc.core.model.Player
import com.mkielar.szewc.core.viewmodel.GameViewModel
import com.mkielar.szewc.databinding.FragmentGameBinding
import io.socket.client.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject


class NetGameFragment : Fragment() {
    private val viewModel: GameViewModel by viewModels()
    private val args: NetGameFragmentArgs by navArgs()

    private lateinit var fragmentGameBinding: FragmentGameBinding
    private lateinit var gridView: GridView

    private val mSocket = IO.socket("https://szewc-ino.herokuapp.com")
    private var myColor = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentGameBinding = FragmentGameBinding.inflate(inflater)
        return fragmentGameBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dialog = ProgressDialog.show(
            requireContext(),
            "Oczekiwanie...",
            "Oczekiwanie na drugiego gracza",
            true,
            true
        ) {
            findNavController().popBackStack()
        }

        dialog.show()

        gridView = GridView(requireContext()).apply {
            layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, Gravity.CENTER)
            verticalCallback = {
                if(viewModel.players[viewModel.getCurrentPlayerIndex()].color == myColor)
                    mSocket.emit("move", "0;$it")
            }
            horizontalCallback = {
                if(viewModel.players[viewModel.getCurrentPlayerIndex()].color == myColor)
                    mSocket.emit("move", "1;$it")
            }
        }

        fragmentGameBinding.container.addView(gridView)

        viewModel.scoreUpdateCallback = {
            fragmentGameBinding.p1Score.text = viewModel.players[0].points.toString()
            fragmentGameBinding.p2Score.text = viewModel.players[1].points.toString()
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
                Toast.makeText(requireContext(), "Wygrał ${it.first().nick}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Remis", Toast.LENGTH_SHORT).show()
            }

            viewLifecycleOwner.lifecycleScope.launch {
                delay(2000)
                findNavController().popBackStack()
            }
        }

        mSocket.apply {
            on("connect") {
                emit("login", args.playerName)
            }
            on("color") {
                myColor = it[0] as Int
            }
            on("start") { data ->
                val players = JSONArray(data[0] as String)
                viewModel.players = List(players.length()) {
                    val player = players.get(it) as JSONObject
                    return@List Player(player.getString("nick"), player.getInt("color"))
                }
                activity?.runOnUiThread {
                    fragmentGameBinding.p1Nick.text = viewModel.players[0].nick
                    fragmentGameBinding.p2Nick.text = viewModel.players[1].nick
                    fragmentGameBinding.p1Score.text = "0"
                    fragmentGameBinding.p2Score.text = "0"
                    fragmentGameBinding.p1Card.setCardBackgroundColor(viewModel.players[0].color)

                    dialog.dismiss()
                }
            }
            on("move") {
                val data = it[0] as String
                val new = data.split(';')
                activity?.runOnUiThread {
                    when (new[0]) {
                        "0" -> viewModel.verticalTouch(new[1].toInt())
                        "1" -> viewModel.horizontalTouch(new[1].toInt())
                    }
                }
            }
            on("disconnect") {
                activity?.runOnUiThread {
                    context?.let {
                        Toast.makeText(it, "Połączenie zostało niespodziewanie zakończone przez serwer, gra zakończona", Toast.LENGTH_LONG).show()
                        findNavController().popBackStack()
                    }
                }
            }
            connect()
        }

        viewModel.startGame()
    }

    override fun onDestroy() {
        mSocket.close()
        super.onDestroy()
    }
}