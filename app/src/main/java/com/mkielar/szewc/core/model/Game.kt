package com.mkielar.szewc.core.model

data class Game(
    val grid: Grid,
    val players: List<Player>,
    var turnCounter: Int = 0,
    var offsetCounter: Int = 0
)
