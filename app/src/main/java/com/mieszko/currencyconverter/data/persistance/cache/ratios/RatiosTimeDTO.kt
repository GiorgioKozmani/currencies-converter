package com.mieszko.currencyconverter.data.persistance.cache.ratios

import com.mieszko.currencyconverter.data.model.CurrencyRatioDTO
import java.util.Date

// todo introduce mappers
// todo REMOVE THIS MODEL
data class RatiosTimeDTO(val ratios: List<CurrencyRatioDTO>, val date: Date)
