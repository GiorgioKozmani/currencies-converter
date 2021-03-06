package com.mieszko.currencyconverter.domain.usecase.trackedcodes

import com.mieszko.currencyconverter.common.model.SupportedCode
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud.IGetTrackedCodesOnceUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud.ISaveTrackedCodesUseCase
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*

class SwapTrackedCodesUseCase(
    private val getTrackedCodesOnceUseCase: IGetTrackedCodesOnceUseCase,
    private val saveTrackedCodesUseCase: ISaveTrackedCodesUseCase
) : ISwapTrackedCodesUseCase {
    override fun invoke(firstCurrency: SupportedCode, secondCurrency: SupportedCode): Completable =
        getTrackedCodesOnceUseCase()
            .subscribeOn(Schedulers.io())
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
                    .subscribeOn(Schedulers.computation())
                    .flatMapCompletable { codes ->
                        saveTrackedCodesUseCase(codes)
                            .subscribeOn(Schedulers.io())
                    }
            }
}

interface ISwapTrackedCodesUseCase {
   operator fun invoke(firstCurrency: SupportedCode, secondCurrency: SupportedCode): Completable
}
