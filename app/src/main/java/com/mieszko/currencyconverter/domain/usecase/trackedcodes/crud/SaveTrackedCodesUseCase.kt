package com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud

import com.mieszko.currencyconverter.common.SupportedCode
import com.mieszko.currencyconverter.domain.repository.ITrackedCodesRepository
import io.reactivex.rxjava3.core.Completable

class SaveTrackedCodesUseCase(
    private val trackedCodesRepository: ITrackedCodesRepository
) : ISaveTrackedCodesUseCase {

    override fun invoke(trackedCodes: List<SupportedCode>): Completable =
        trackedCodesRepository
            .saveTrackedCodes(trackedCodes)
}

interface ISaveTrackedCodesUseCase {
    operator fun invoke(trackedCodes: List<SupportedCode>): Completable
}
