package com.mieszko.currencyconverter.data.model

import com.google.gson.annotations.SerializedName

//todo rethink where this model should belong
data class CodeData(
    @SerializedName("name") val name: String,
    @SerializedName("flagUrl") val flagUrl: String
)