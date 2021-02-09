package com.mkielar.szewc.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Player(
    val nick: String,
    val color: Int,
    var points: Int = 0
) : Parcelable
