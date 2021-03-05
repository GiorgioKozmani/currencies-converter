package com.mieszko.currencyconverter.domain.model

import com.mieszko.currencyconverter.common.SupportedCode
import java.util.*

data class RatiosTime(val ratios: EnumMap<SupportedCode, Double>, val time: Date)