package com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud

import com.mieszko.currencyconverter.common.SupportedCode
import com.mieszko.currencyconverter.domain.repository.ITrackedCodesRepository
import io.reactivex.rxjava3.core.Single

//TODO DI
class GetTrackedCodesOnceUseCase(
    private val trackedCodesRepository: ITrackedCodesRepository
) : IGetTrackedCodesOnceUseCase {

    //todo consider threading
    override fun invoke(): Single<List<SupportedCode>> =
        trackedCodesRepository
            .getTrackedCodesOnce()
}

interface IGetTrackedCodesOnceUseCase {
    operator fun invoke(): Single<List<SupportedCode>>
}
