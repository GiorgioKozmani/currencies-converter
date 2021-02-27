package com.mieszko.currencyconverter.domain.repository

import com.mieszko.currencyconverter.common.Resource
import com.mieszko.currencyconverter.data.model.CurrencyRatioDTO
import com.mieszko.currencyconverter.data.persistance.cache.ratios.RatiosTimeDTO
import com.mieszko.currencyconverter.domain.model.RatiosTime
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single


interface IRatiosRepository {
    //todo this is clearly wrong, this should not return DTO.
    // TODO JUST REMOVE REPOSITORY
    fun observeRatios(): Observable<Resource<RatiosTimeDTO>>
    fun fetchRemoteRatios(): Completable
}