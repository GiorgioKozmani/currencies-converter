package com.mieszko.currencyconverter.domain.repository

import com.mieszko.currencyconverter.data.persistance.cache.ratios.RatiosTimeDTO
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface IRatiosRepository {
    // todo this is clearly wrong, this should not return DTO.
    // TODO JUST REMOVE REPOSITORY
    fun observeRatios(): Observable<RatiosTimeDTO>
    fun fetchRemoteRatios(): Completable
}
