package com.mieszko.currencyconverter.data.repository

import com.mieszko.currencyconverter.common.SupportedCode
import com.mieszko.currencyconverter.data.api.CurrenciesApi
import com.mieszko.currencyconverter.data.persistance.IRatiosCache
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*

class RatiosRepository(
    private val currenciesApi: CurrenciesApi,
    private val cache: IRatiosCache
) : IRatiosRepository {

    override fun loadCurrenciesRatios(): Single<EnumMap<SupportedCode, Double>> {
        //todo redesign the cache
        //TODO CACHE IMPL:
        // ALWAYS DO NETWORK REQUEST:
        // - ON SUCCESS EMIT SUCCESS RESOURCE + CACHE
        // - ON ERROR EMIT CACHE (OR NULL IF EMPTY) + SHOW PROPER ERROR (NO DATA / STALE DATA IF PRESENT)
        return currenciesApi
            .getCurrencies()
            .doOnSuccess { cache.cacheRatios(it) }
                // TODO HANDLE CASE THAT CACHE IS EMPTY!
                // TODO EMIT RESOURCE INSTEAD, SO WE KNOW WHEN IT'S NOT FRESH
                // TODO IF IT IS THE CASE, SHOW TOAST FOR NOW, IN FUTURE EMPLATELIKE
            .onErrorResumeNext { cache.getCachedRatios().subscribeOn(Schedulers.computation()) }
            .flatMap { list ->
                Single.fromCallable {
                    val supportedRatios: EnumMap<SupportedCode, Double> =
                        EnumMap(SupportedCode::class.java)
                    list.forEach {
                        try {
                            supportedRatios[it.code] = it.ratioToUAH
                        } catch (e: Exception) {
                        }
                    }
                    supportedRatios
                }
                    .subscribeOn(Schedulers.computation())
            }
    }
}

interface IRatiosRepository {
    fun loadCurrenciesRatios(): Single<EnumMap<SupportedCode, Double>>
}


