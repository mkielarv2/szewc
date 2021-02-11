package com.mkielar.szewc.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Game(
    val grid: Grid,
    val players: List<Player>,
    var turnCounter: Int = 0,
    var offsetCounter: Int = 0
) : Parcelable
