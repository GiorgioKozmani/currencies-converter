package com.mieszko.currencyconverter.data.model

import com.mieszko.currencyconverter.common.SupportedCurrency

//TODO THIS SHOULD CONTAIN ONLY CURRENCY AND STRINGS? SO THERE'S 0 LOGIC DOWN IN VH / ADAPTER
data class CurrencyListItemModel(
    val currency: SupportedCurrency,
    var amount: Double = 0.0,
    var baseToThisText: String = "",
    var thisToBaseText: String = ""
)

