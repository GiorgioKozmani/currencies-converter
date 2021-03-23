package com.mieszko.currencyconverter.data.model

import com.google.gson.annotations.SerializedName
import com.mieszko.currencyconverter.common.model.SupportedCode

// todo for this size of projcet DTO might be an overkill
data class CurrencyRatioDTO(
    @SerializedName("cc")
    var code: SupportedCode,
    @SerializedName("rate")
    var ratioToUAH: Double
)
