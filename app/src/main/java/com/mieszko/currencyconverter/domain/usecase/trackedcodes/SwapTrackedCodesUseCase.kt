package com.mieszko.currencyconverter.domain.usecase.trackedcodes

import com.mieszko.currencyconverter.common.SupportedCode
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
        //todo get threading right
        getTrackedCodesOnceUseCase()
            .flatMapCompletable { currentTrackedCodes ->
                //todo check thread
                Single.fromCallable {
                    currentTrackedCodes
                        //todo this might not work because of also, check out
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
                    //todo check thread
                    .flatMapCompletable { codes ->
                        saveTrackedCodesUseCase(codes)
                            .subscribeOn(Schedulers.io())
                    }
            }
}

interface ISwapTrackedCodesUseCase {
   operator fun invoke(firstCurrency: SupportedCode, secondCurrency: SupportedCode): Completable
}
