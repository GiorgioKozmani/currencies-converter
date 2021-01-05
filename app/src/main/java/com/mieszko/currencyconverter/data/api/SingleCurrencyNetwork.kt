package com.mieszko.currencyconverter.data.api

import com.google.gson.annotations.SerializedName

// TODO RENAME, add mapper for DTO, where DTO will figure out it's name, full name, image, maybe ratiotobase?
data class SingleCurrencyNetwork(
    @SerializedName("cc")
    var shortName: String,
    @SerializedName("rate")
    var ratioToUAH: Double
)