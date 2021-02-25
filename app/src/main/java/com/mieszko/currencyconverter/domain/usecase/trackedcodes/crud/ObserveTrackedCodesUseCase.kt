package com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud

import com.mieszko.currencyconverter.common.SupportedCode
import com.mieszko.currencyconverter.domain.repository.ITrackedCodesRepository
import io.reactivex.rxjava3.core.Observable

//TODO DI
class ObserveTrackedCodesUseCase(
    private val trackedCodesRepository: ITrackedCodesRepository
) : IObserveTrackedCodesUseCase {

    override fun invoke(): Observable<List<SupportedCode>> =
        trackedCodesRepository
            .observeTrackedCodes()
}

interface IObserveTrackedCodesUseCase {
    operator fun invoke(): Observable<List<SupportedCode>>
}
