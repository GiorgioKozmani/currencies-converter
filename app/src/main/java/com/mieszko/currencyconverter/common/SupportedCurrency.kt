package com.mieszko.currencyconverter.common

import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName
import com.mieszko.currencyconverter.R
import java.util.*

enum class SupportedCode { AUD, BGN, BRL, CAD, CHF, CNY, CZK, DKK, EUR, GBP, HKD, HRK, HUF, ILS, INR, JPY, KRW, MXN, MYR, NOK, NZD, PLN, RON, RUB, SEK, SGD, THB, TRY, USD, ZAR, UAH, IDR, KZT, MDL, SAR, EGP, BYN, AZN, DZD, BDT, AMD, KGS, LBP, LYD, VND, AED, TND, UZS, TWD, GHS, RSD, TJS, GEL }

data class CodeData(
    @StringRes @SerializedName("name") val name: Int,
    @SerializedName("flagUrl") val flagUrl: String
)
