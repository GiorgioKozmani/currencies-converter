package com.mieszko.currencyconverter.data.api.model

import com.google.gson.annotations.SerializedName
import com.mieszko.currencyconverter.common.SupportedCode

data class CurrencyRatioDTO(
    @SerializedName("cc")
    var code: SupportedCode,
    @SerializedName("rate")
    var ratioToUAH: Double
)