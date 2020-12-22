package com.mieszko.currencyconverter.data.model

import com.mieszko.currencyconverter.util.SupportedCurrency

data class CurrencyListItemModel(
    val currency: SupportedCurrency,
    var amount: Double = 0.0
)

