package com.mieszko.currencyconverter.data.model

import com.mieszko.currencyconverter.common.SupportedCurrency

//todo this if i can optimise models
data class CurrencyData(
    val currency: SupportedCurrency,
    var toUAHRatio: Double = 0.0,
    var amount: Double = 0.0
)
