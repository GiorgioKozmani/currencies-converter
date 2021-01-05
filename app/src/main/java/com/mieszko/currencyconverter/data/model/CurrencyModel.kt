package com.mieszko.currencyconverter.data.model

import com.mieszko.currencyconverter.common.SupportedCurrency

data class CurrencyModel(
    val currency: SupportedCurrency,
    var toUAHRatio: Double = 0.0,
    var amount: Double = 0.0
)
