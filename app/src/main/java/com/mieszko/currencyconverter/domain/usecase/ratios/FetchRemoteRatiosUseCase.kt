package com.mieszko.currencyconverter.domain.usecase.ratios

import com.mieszko.currencyconverter.domain.repository.IRatiosRepository
import io.reactivex.rxjava3.core.Completable

//TODO REWORK! SO IT RETURNS IT TOGETHER WITH THE DATA!!!
class FetchRemoteRatiosUseCase(
    private val ratiosRepository: IRatiosRepository
) : IFetchRemoteRatiosUseCase {

    override fun invoke(): Completable =
        ratiosRepository.fetchRemoteRatios()
}

interface IFetchRemoteRatiosUseCase {
    operator fun invoke(): Completable
}
