package com.mieszko.currencyconverter.domain.usecase.trackedcodes

import com.mieszko.currencyconverter.common.model.SupportedCode
import com.mieszko.currencyconverter.domain.repository.ITrackedCodesRepository
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud.ISaveTrackedCodesUseCase
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Collections

class SwapTrackedCodesUseCase(
    private val trackedCodesRepository: ITrackedCodesRepository,
    private val saveTrackedCodesUseCase: ISaveTrackedCodesUseCase
) : ISwapTrackedCodesUseCase {

    override fun invoke(firstCurrency: SupportedCode, secondCurrency: SupportedCode): Completable =
        trackedCodesRepository.observeTrackedCodes()
            .firstOrError()
            .observeOn(Schedulers.io())
            .flatMapCompletable { currentTrackedCodes ->
                Single.fromCallable {
                    currentTrackedCodes
                        .toMutableList()
                        .also { currentList ->
                            Collections.swap(
                                currentList,
                                currentList.indexOf(firstCurrency),
                                currentList.indexOf(secondCurrency)
                            )
                        }
                }
                    .flatMapCompletable { codes -> saveTrackedCodesUseCase(codes) }
            }
}

interface ISwapTrackedCodesUseCase {
    operator fun invoke(firstCurrency: SupportedCode, secondCurrency: SupportedCode): Completable
}
