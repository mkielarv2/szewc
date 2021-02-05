package com.mkielar.szewc.core.model

data class Player(
    val nick: String,
    val color: Int,
    var points: Int = 0
)
