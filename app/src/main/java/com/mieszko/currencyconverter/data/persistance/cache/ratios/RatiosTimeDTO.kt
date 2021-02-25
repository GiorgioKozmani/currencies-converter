package com.mieszko.currencyconverter.data.persistance.cache.ratios

import com.mieszko.currencyconverter.data.model.CurrencyRatioDTO

//todo introduce mappers
data class RatiosTimeDTO(val ratios: List<CurrencyRatioDTO>, val time: Long)