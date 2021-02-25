package com.mieszko.currencyconverter.domain.repository

import com.mieszko.currencyconverter.common.Resource
import com.mieszko.currencyconverter.domain.model.RatiosTime
import io.reactivex.rxjava3.core.Single


interface IRatiosRepository {
    fun loadCurrenciesRatios(): Single<Resource<RatiosTime>>
}