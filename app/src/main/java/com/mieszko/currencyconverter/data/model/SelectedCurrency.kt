package com.mieszko.currencyconverter.data.model

import com.mieszko.currencyconverter.common.SupportedCurrency

data class SelectedCurrency(
    val currency: SupportedCurrency,
    //todo think whether it's better to keep it mutable or copy object instead
    val isTracked: Boolean
)
