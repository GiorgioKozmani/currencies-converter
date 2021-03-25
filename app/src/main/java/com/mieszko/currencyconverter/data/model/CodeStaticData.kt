package com.mieszko.currencyconverter.data.model

import androidx.annotation.DrawableRes
import com.google.gson.annotations.SerializedName

data class CodeStaticData(
    @SerializedName("name") val name: String,
    @SerializedName("flag_res_id") @DrawableRes val flagResId: Int
)
