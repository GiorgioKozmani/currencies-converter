package com.mieszko.currencyconverter.domain.usecase.trackedcodes

import com.mieszko.currencyconverter.common.model.SupportedCode
import com.mieszko.currencyconverter.domain.repository.ITrackedCodesRepository
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud.ISaveTrackedCodesUseCase
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class MoveTrackedCodeToTopUseCase(
    private val trackedCodesRepository: ITrackedCodesRepository,
    private val saveTrackedCodesUseCase: ISaveTrackedCodesUseCase
) : IMoveTrackedCodeToTopUseCase {

    override fun invoke(codeToMove: SupportedCode): Completable =
        trackedCodesRepository.observeTrackedCodes()
            .firstOrError()
            .observeOn(Schedulers.io())
            .flatMapCompletable { currentTrackedCodes ->
                Single.fromCallable {
                    if (currentTrackedCodes.contains(codeToMove)) {
                        currentTrackedCodes.toMutableList()
                            .apply {
                                remove(codeToMove)
                                add(0, codeToMove)
                            }
                    } else {
                        currentTrackedCodes
                    }
                }
                    .flatMapCompletable { codes -> saveTrackedCodesUseCase(codes) }
            }
}

interface IMoveTrackedCodeToTopUseCase {
    operator fun invoke(codeToMove: SupportedCode): Completable
}
