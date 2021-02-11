package com.mkielar.szewc.core.viewmodel

import android.graphics.Color
import androidx.lifecycle.ViewModel
import com.mkielar.szewc.core.model.*
import io.socket.client.IO
import org.json.JSONObject


class NetGameViewModel : ViewModel() {
    private var players : List<Player> = listOf(Player("Adam", Color.RED), Player("Eve", Color.BLUE))
    private val gridSize = 3
    private val mSocket = IO.socket("http://172.21.9.65:3000") // TODO replace placeholder
    private var myColor = Color.RED

    private var game: Game

    var gridUpdateCallback: ((Grid) -> Unit)? = null
    var endGameCallback: ((List<Player>) -> Unit)? = null

    init {
        val vertical = List(gridSize * (gridSize + 1)) { Line(null) }
        val horizontal = List(gridSize * (gridSize + 1)) { Line(null) }
        val cells = List(gridSize * gridSize) { Cell(null) }
        val grid = Grid(vertical, horizontal, cells, gridSize)
        game = Game(grid, players)
        mSocket.connect()
        mSocket.on("connect") {
            mSocket.emit("joined the game", "Username") // TODO replace placeholder
        }
        mSocket.on("game started") {
            val data = it[0]
            println(data)
            val vertical = List(gridSize * (gridSize + 1)) { Line(null) }
            val horizontal = List(gridSize * (gridSize + 1)) { Line(null) }
            val cells = List(gridSize * gridSize) { Cell(null) }
            val grid = Grid(vertical, horizontal, cells, gridSize)
            game = Game(grid, players)
        }
        mSocket.on("move") {
            val data = it[0] as String
            val new = data.split(';')
            println(new[0])
            when(new[0]) {
                "0" -> updateLineOf(game.grid.vertical, new[1].toInt())
                "1" -> updateLineOf(game.grid.horizontal, new[1].toInt())
            }
        }
        mSocket.on("end") {
            val data = it[0] as JSONObject
            println(data)
            endGameCallback?.invoke(
                players.filter { player ->
                    player.points == players.maxOf { it.points }
                }
            )
        }
    }

    fun startGame() {
        requestDrawGrid()
    }

    fun verticalTouch(index: Int) {
        if(players[getCurrentPlayerIndex()].color == myColor)
        mSocket.emit("move", "0;$index")
        //updateLineOf(game.grid.vertical, index)
    }

    fun horizontalTouch(index: Int) {
        if(players[getCurrentPlayerIndex()].color == myColor)
        mSocket.emit("move", "1;$index")
        //updateLineOf(game.grid.horizontal, index)
    }

    private fun updateLineOf(
        lineList: List<Line>,
        index: Int
    ) {
        if (index !in lineList.indices) return
        if (lineList[index].owner == null) {
            lineList[index].owner = players[getCurrentPlayerIndex()]
            game.turnCounter++

            calculateClosedCells()
            checkEndGame()
            requestDrawGrid()
        }
    }

    private fun calculateClosedCells() {
        val vertical = game.grid.vertical
        val horizontal = game.grid.horizontal
        var doubleCellPlayer: Player? = null
        for (i in 0 until gridSize) {
            for (j in 0 until gridSize) {
                if (vertical[i * gridSize + j].owner == null || vertical[(i + 1) * gridSize + j].owner == null ||
                    horizontal[j * gridSize + i].owner == null || horizontal[(j + 1) * gridSize + i].owner == null
                ) continue

                if (game.grid.cells[i * gridSize + j].owner == null) {
                    if (doubleCellPlayer == null) {
                        game.offsetCounter += game.players.size - 1
                        val player = players[getCurrentPlayerIndex()]
                        game.grid.cells[i * gridSize + j].owner = player
                        player.points++
                        doubleCellPlayer = player
                    } else {
                        game.grid.cells[i * gridSize + j].owner = doubleCellPlayer
                        doubleCellPlayer.points++
                    }
                }
            }
        }
    }

    private fun checkEndGame() {
        if (game.grid.vertical
                .map { it.owner }
                .all { it != null }
                .and(game.grid.horizontal
                    .map { it.owner }
                    .all { it != null }
                )
        ) {
            endGameCallback?.invoke(
                players.filter { player ->
                    player.points == players.maxOf { it.points }
                }
            )
        }
    }

    private fun getCurrentPlayerIndex() =
        (game.turnCounter + game.offsetCounter) % 2 //game.players.size

    private fun requestDrawGrid() {
        gridUpdateCallback?.invoke(game.grid)
    }
}