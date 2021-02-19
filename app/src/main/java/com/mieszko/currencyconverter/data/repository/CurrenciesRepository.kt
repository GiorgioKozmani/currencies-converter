package com.mieszko.currencyconverter.data.repository

import com.mieszko.currencyconverter.common.SupportedCurrency
import com.mieszko.currencyconverter.data.persistance.ICurrenciesCache
import com.mieszko.currencyconverter.data.util.CurrenciesApi
import io.reactivex.rxjava3.core.Single
import java.util.*

class CurrenciesRepository(
    private val currenciesApi: CurrenciesApi,
    private val cache: ICurrenciesCache
) : ICurrenciesRepository {

    override fun loadCurrencies(): Single<EnumMap<SupportedCurrency, Double>> {
        //todo redesign the cache
//        return Observable
//            .concatArrayEagerDelayError(
//                cache
//                    .getCurrencies()
//                    .toObservable(),
//                currenciesApi
//                    .getCurrencies()
//                    .doOnSuccess { cache.saveCurrencies(it) }
//                    .toObservable()
//            )
        //todo create mapper
        return currenciesApi
            .getCurrencies()
            .map { list ->
                val supportedRatios: EnumMap<SupportedCurrency, Double> = EnumMap(SupportedCurrency::class.java)
                list.forEach {
                    try {
                        supportedRatios.put(SupportedCurrency.valueOf(it.shortName), it.ratioToUAH)
                    } catch (e: Exception) {
                    }
                }
                supportedRatios
            }
    }
}

interface ICurrenciesRepository {
    fun loadCurrencies(): Single<EnumMap<SupportedCurrency, Double>>
}


