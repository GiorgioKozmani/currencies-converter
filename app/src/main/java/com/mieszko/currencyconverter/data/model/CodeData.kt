package com.mieszko.currencyconverter.data.model

import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName

//todo rethink where this model should belong
data class CodeData(
    @StringRes @SerializedName("name") val name: Int,
    @SerializedName("flagUrl") val flagUrl: String
)