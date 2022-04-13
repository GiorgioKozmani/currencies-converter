package com.mieszko.currencyconverter.data.repository

import com.mieszko.currencyconverter.common.model.SupportedCode
import com.mieszko.currencyconverter.data.api.CurrenciesApi
import com.mieszko.currencyconverter.data.persistance.IRatiosCache
import com.mieszko.currencyconverter.domain.analytics.IFirebaseEventsLogger
import com.mieszko.currencyconverter.domain.model.RatiosTime
import com.mieszko.currencyconverter.domain.repository.IRatiosRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.EnumMap

class RatiosRepository(
    private val currenciesApi: CurrenciesApi,
    private val cache: IRatiosCache,
    private val eventsLogger: IFirebaseEventsLogger
) : IRatiosRepository {

//    The important thing you need to understand is,
//    whether it comes from the network that goes into the disk,
//    it NEVER goes back to the UI. This makes the disk your single source of truth,
//    any time where this changes, your UI always shows the latest information.

    override fun observeRatios(): Observable<RatiosTime> {
        // DTOs -> DomainModels conversion takes place here
        return cache.observeCodeRatios()
            .observeOn(Schedulers.io())
            .flatMapSingle { ratiosDateDto ->
                Single.fromCallable {
                    val supportedRatios: EnumMap<SupportedCode, Double> =
                        EnumMap(SupportedCode::class.java)

                    ratiosDateDto.ratios
                        .also { supportedRatios[SupportedCode.UAH] = 1.0 }
                        .forEach {
                            try {
                                supportedRatios[it.code] = it.ratioToUAH
                            } catch (e: Exception) {
                                eventsLogger.logNonFatalError(
                                    throwable = e,
                                    customMessage = "RATIOSTIME OBJECT CREATION ERROR, CODE: " + it.code + "  RATIO: " + it.ratioToUAH
                                )
                            }
                        }

                    RatiosTime(supportedRatios, ratiosDateDto.date)
                }
            }
    }

    override fun fetchRemoteRatios(): Completable {
        return currenciesApi.getCodeRatios()
            .observeOn(Schedulers.io())
            .flatMapCompletable { networkRatios ->
                cache.saveCodeRatios(networkRatios)
            }
    }
}
