package com.bytebandits.krishiaayog

import android.annotation.SuppressLint

object Utils {
    @SuppressLint("DefaultLocale")
    fun mstokmh(speed: Double): String {
        //upto decimal places only
        return String.format("%.2f", speed * 3.6)

    }
}