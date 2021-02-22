package com.mieszko.currencyconverter.domain.repository

import com.mieszko.currencyconverter.common.SupportedCode
import io.reactivex.rxjava3.core.Single
import java.util.*


interface IRatiosRepository {
    fun loadCurrenciesRatios(): Single<EnumMap<SupportedCode, Double>>
}