package com.mieszko.currencyconverter.data.api

data class CurrenciesResponse(
    val base: String,
    val rates: MutableList<SingleCurrencyNetwork>
)