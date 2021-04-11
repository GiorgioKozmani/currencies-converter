package com.mieszko.currencyconverter.domain.usecase.ratios

import com.mieszko.currencyconverter.common.model.SupportedCode
import com.mieszko.currencyconverter.domain.analytics.IFirebaseEventsLogger
import com.mieszko.currencyconverter.domain.model.RatiosTime
import com.mieszko.currencyconverter.domain.repository.IRatiosRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.util.EnumMap

class ObserveRatiosUseCase(
    private val ratiosRepository: IRatiosRepository,
    private val eventsLogger: IFirebaseEventsLogger
) : IObserveRatiosUseCase {

    override fun invoke(): Observable<RatiosTime> =
        ratiosRepository.observeRatios()
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
                                eventsLogger.logNonFatalError(e, "RATIOSTIME OBJECT CREATION ERROR")
                            }
                        }

                    RatiosTime(supportedRatios, ratiosDateDto.date)
                }
            }
}

interface IObserveRatiosUseCase {
    operator fun invoke(): Observable<RatiosTime>
}
