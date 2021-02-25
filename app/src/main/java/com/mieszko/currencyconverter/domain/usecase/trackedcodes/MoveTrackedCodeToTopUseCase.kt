package com.mieszko.currencyconverter.domain.usecase.trackedcodes

import com.mieszko.currencyconverter.common.SupportedCode
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud.IGetTrackedCodesOnceUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud.ISaveTrackedCodesUseCase
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class MoveTrackedCodeToTopUseCase(
    private val getTrackedCodesOnceUseCase: IGetTrackedCodesOnceUseCase,
    private val saveTrackedCodesUseCase: ISaveTrackedCodesUseCase
) : IMoveTrackedCodeToTopUseCase {
    override fun invoke(codeToMove: SupportedCode): Completable =
        //todo get threading right
        getTrackedCodesOnceUseCase()
            .flatMapCompletable { currentTrackedCodes ->
                //todo check thread
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
                    .subscribeOn(Schedulers.computation())
                    //todo check thread
                    .flatMapCompletable { codes ->
                        saveTrackedCodesUseCase(codes)
                            .subscribeOn(Schedulers.io())
                    }
            }
}

interface IMoveTrackedCodeToTopUseCase {
    operator fun invoke(codeToMove: SupportedCode): Completable
}