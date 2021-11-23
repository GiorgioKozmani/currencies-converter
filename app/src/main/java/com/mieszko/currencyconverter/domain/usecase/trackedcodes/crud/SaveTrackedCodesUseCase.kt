package com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud

import com.mieszko.currencyconverter.common.model.SupportedCode
import com.mieszko.currencyconverter.domain.analytics.IFirebaseEventsLogger
import com.mieszko.currencyconverter.domain.repository.ITrackedCodesRepository
import io.reactivex.rxjava3.core.Completable

class SaveTrackedCodesUseCase(
    private val trackedCodesRepository: ITrackedCodesRepository,
    private val eventsLogger: IFirebaseEventsLogger
) : ISaveTrackedCodesUseCase {

    override fun invoke(trackedCodes: List<SupportedCode>): Completable =
        trackedCodesRepository
            .saveTrackedCodes(trackedCodes)
            .doOnComplete { eventsLogger.setBaseCurrencyUserProperty(trackedCodes.firstOrNull()) }
}

interface ISaveTrackedCodesUseCase {
    operator fun invoke(trackedCodes: List<SupportedCode>): Completable
}
