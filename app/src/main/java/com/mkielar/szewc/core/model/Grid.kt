package com.mkielar.szewc.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Grid(
    val vertical: List<Line>,
    val horizontal: List<Line>,
    val cells: List<Cell>,
    val size: Int
) : Parcelable
