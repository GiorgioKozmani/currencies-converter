package com.mieszko.currencyconverter.domain.repository

import com.mieszko.currencyconverter.domain.model.RatiosTime
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface IRatiosRepository {
    fun observeRatios(): Observable<RatiosTime>
    fun fetchRemoteRatios(): Completable
}
