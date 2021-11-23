package com.mieszko.currencyconverter.domain.usecase.ratios

import com.mieszko.currencyconverter.common.model.SupportedCode
import com.mieszko.currencyconverter.domain.util.roundToDecimals
import java.math.BigDecimal
import java.math.MathContext

class MakeRatioStringUseCase : IMakeRatioStringUseCase {

    override fun invoke(
        firstCurrencyCode: SupportedCode,
        firstCurrencyToUahRatio: Double,
        secondCurrencyCode: SupportedCode,
        secondCurrencyToUahRatio: Double
    ): String = if (secondCurrencyToUahRatio != 0.0) {
        getReadableRatio(firstCurrencyToUahRatio, secondCurrencyToUahRatio).let {
            "${it.first} $firstCurrencyCode ≈ ${it.second.roundToDecimals(3)} $secondCurrencyCode"
        }
    } else {
        // todo TO CRASHLYTICS
        ""
    }

    private fun getReadableRatio(
        firstCurrencyToUahRatio: Double,
        secondCurrencyToUahRatio: Double
    ): Pair<Int, Double> {
        var roundedRatio = BigDecimal(firstCurrencyToUahRatio / secondCurrencyToUahRatio)
        // round to 3 significant figures
        roundedRatio = roundedRatio.round(MathContext(3))

        return prettifyRatio(Pair(1, roundedRatio.toDouble()))
    }

    /**
     * Recursive method that multiplies both Pair(A: Int, B: Double) values by 10
     * until B >= 0.01
     * for example Pair(1, 0.00001) into Pair(100, 0.001)
     */
    private fun prettifyRatio(pair: Pair<Int, Double>): Pair<Int, Double> {
        return if (pair.second < 0.01) {
            prettifyRatio(Pair(pair.first * 10, pair.second * 10))
        } else {
            pair
        }
    }
}

interface IMakeRatioStringUseCase {
    operator fun invoke(
        firstCurrencyCode: SupportedCode,
        firstCurrencyToUahRatio: Double,
        secondCurrencyCode: SupportedCode,
        secondCurrencyToUahRatio: Double
    ): String
}
