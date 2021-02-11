package com.mkielar.szewc.core.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Line(
    var owner: Player?
) : Parcelable
