package com.mieszko.currencyconverter.data.model

import com.mieszko.currencyconverter.common.SupportedCurrency

data class CurrencyRatio(
    val currency: SupportedCurrency,
    var toUAHRatio: Double
)
