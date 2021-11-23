package com.mieszko.currencyconverter.data.repository

import com.mieszko.currencyconverter.data.api.CurrenciesApi
import com.mieszko.currencyconverter.data.model.RatiosTimeDTO
import com.mieszko.currencyconverter.data.persistance.IRatiosCache
import com.mieszko.currencyconverter.domain.repository.IRatiosRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

// TODO REMOVE THIS REPOSITORY, REPLACE WITH USECASES
class RatiosRepository(
    private val currenciesApi: CurrenciesApi,
    private val cache: IRatiosCache
) : IRatiosRepository {
//    https://proandroiddev.com/anemic-repositories-mvi-and-rxjava-induced-design-damage-and-how-aac-viewmodel-is-silently-1762caa70e13
//    The important thing you need to understand is,
//    whether it comes from the network that goes into the disk,
//    it NEVER goes back to the UI. This makes the disk your single source of truth,
//    any time where this changes, your UI always shows the latest information.

    override fun observeRatios(): Observable<RatiosTimeDTO> {
        return cache.observeCodeRatios()
    }

    override fun fetchRemoteRatios(): Completable {
        return currenciesApi.getCodeRatios()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .flatMapCompletable { networkRatios ->
                cache.saveCodeRatios(networkRatios)
            }
    }
}
