package com.mkielar.szewc.core.model

data class Grid(
    val vertical: List<Line>,
    val horizontal: List<Line>,
    val cells: List<Cell>,
    val size: Int
)
