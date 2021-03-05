package com.mieszko.currencyconverter.data.model

import androidx.annotation.DrawableRes
import com.google.gson.annotations.SerializedName

//todo rethink where this model should belong
data class CodeData(
    @SerializedName("name") val name: String,
    @SerializedName("flag_res_id") @DrawableRes val flagResId: Int
)