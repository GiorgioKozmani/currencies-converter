package com.mieszko.currencyconverter.data.repository

import com.mieszko.currencyconverter.data.api.SingleCurrencyNetwork
import com.mieszko.currencyconverter.data.persistance.ICurrenciesCache
import com.mieszko.currencyconverter.data.util.CurrenciesApi
import io.reactivex.rxjava3.core.Observable

class CurrenciesRepository(
    private val currenciesApi: CurrenciesApi,
    private val cache: ICurrenciesCache
) : ICurrenciesRepository {

    override fun loadCurrencies(): Observable<List<SingleCurrencyNetwork>> {
        //todo redesign the cache
        return Observable
            .concatArrayEagerDelayError(
                cache
                    .getCurrencies()
                    .toObservable(),
                currenciesApi
                    .getCurrencies()
                    .doOnSuccess { cache.saveCurrencies(it) }
                    .toObservable()
            )
    }
}

interface ICurrenciesRepository {
    fun loadCurrencies(): Observable<List<SingleCurrencyNetwork>>
}


