package com.mieszko.currencyconverter.domain.usecase.ratios

import com.mieszko.currencyconverter.common.model.SupportedCode
import com.mieszko.currencyconverter.domain.model.RatiosTime
import com.mieszko.currencyconverter.domain.repository.IRatiosRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.util.*

//TODO REWORK! SO IT RETURNS IT TOGETHER WITH THE DATA!!!
class ObserveRatiosUseCase(
    private val ratiosRepository: IRatiosRepository
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
                                // TODO CRASHLITICS
                            }
                        }

                    RatiosTime(supportedRatios, ratiosDateDto.date)
                }
            }
}

interface IObserveRatiosUseCase {
    operator fun invoke(): Observable<RatiosTime>
}
