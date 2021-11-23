package com.mieszko.currencyconverter.domain.util

import androidx.annotation.WorkerThread
import kotlin.math.pow
import kotlin.math.roundToLong

@WorkerThread
fun Double.roundToDecimals(decimals: Int): Double {
    val factor = 10.0.pow(decimals)
    return (this * factor).roundToLong() / factor
}
