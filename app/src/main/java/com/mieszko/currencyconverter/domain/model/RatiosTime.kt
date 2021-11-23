package com.mieszko.currencyconverter.domain.model

import com.mieszko.currencyconverter.common.model.SupportedCode
import java.util.Date
import java.util.EnumMap

data class RatiosTime(val ratios: EnumMap<SupportedCode, Double>, val time: Date)
