package com.mieszko.currencyconverter.data.repository

import com.mieszko.currencyconverter.common.Resource
import com.mieszko.currencyconverter.data.api.CurrenciesApi
import com.mieszko.currencyconverter.data.persistance.cache.ratios.IRatiosCache
import com.mieszko.currencyconverter.data.persistance.cache.ratios.RatiosTimeDTO
import com.mieszko.currencyconverter.domain.repository.IRatiosRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

//TODO JAVADOC EXPLAINING TE IMPLEMENTATION
// TODO REMOVE THIS REPOSITORY, REPLACE WITH USECASES
class RatiosRepository(
    private val currenciesApi: CurrenciesApi,
    private val cache: IRatiosCache
) : IRatiosRepository {
//    https://proandroiddev.com/anemic-repositories-mvi-and-rxjava-induced-design-damage-and-how-aac-viewmodel-is-silently-1762caa70e13
    // todo !!!!!!!!!!
//    The important thing you need to understand is,
//    whether it comes from the network that goes into the disk,
//    it NEVER goes back to the UI. This makes the disk your single source of truth, any time where this changes, your UI always shows the latest information.
//    **If you do this RxJava operation “pull from network, pull from disk, whoever comes last return it to the user” you are doing it wrong.**

    // TODO ADD SOME STATIC DATA TO CACHE ON FIRST RUN SO SMTH'S AVAILABLE?
    // TODO HANDLE TOO FREQUENT REFRESH CLICKS (INTRODUCE MINUMUM LOADING TIME? )
    override fun observeRatios(): Observable<Resource<RatiosTimeDTO>> {
        return cache.observeCodeRatios()
    }

    override fun fetchRemoteRatios(): Completable {
        return currenciesApi.getCodeRatios()
            .subscribeOn(Schedulers.io())
            .flatMapCompletable { cache.saveTrackedCodes(it) }
    }
}


