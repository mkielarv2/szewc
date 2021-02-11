package com.mkielar.szewc.core.viewmodel

import android.graphics.Color
import androidx.lifecycle.ViewModel
import com.mkielar.szewc.core.model.*

class GameViewModel : ViewModel() {
    private val gridSize = 3

    var gridUpdateCallback: ((Game) -> Unit)? = null
    var endGameCallback: ((List<Player>) -> Unit)? = null
    var scoreUpdateCallback: ((List<Player>) -> Unit)? = null

    private var grid: Grid
    private var game: Game

    var players = listOf(Player("Adam", Color.RED), Player("Eve", Color.BLUE))
        set(value) {
            field = value
            game = Game(grid, value)
        }

    init {
        val vertical = List(gridSize * (gridSize + 1)) { Line(null) }
        val horizontal = List(gridSize * (gridSize + 1)) { Line(null) }
        val cells = List(gridSize * gridSize) { Cell(null) }
        grid = Grid(vertical, horizontal, cells, gridSize)
        game = Game(grid, players)
    }

    fun startGame() {
        requestDrawGrid()
    }

    fun verticalTouch(index: Int) {
        updateLineOf(game.grid.vertical, index)
    }

    fun horizontalTouch(index: Int) {
        updateLineOf(game.grid.horizontal, index)
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
            scoreUpdateCallback?.invoke(game.players)
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

    fun getCurrentPlayerIndex() =
        (game.turnCounter + game.offsetCounter) % game.players.size

    private fun requestDrawGrid() {
        gridUpdateCallback?.invoke(game)
    }
}