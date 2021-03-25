package com.mieszko.currencyconverter.data.persistance.cache.ratios

import com.mieszko.currencyconverter.data.model.CurrencyRatioDTO
import java.util.Date

data class RatiosTimeDTO(val ratios: List<CurrencyRatioDTO>, val date: Date)
