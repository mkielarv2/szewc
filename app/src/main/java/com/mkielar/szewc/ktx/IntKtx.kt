package com.mkielar.szewc

import androidx.core.graphics.ColorUtils

fun Int.setAlphaComponent(alpha: Int): Int =
    ColorUtils.setAlphaComponent(this, alpha)