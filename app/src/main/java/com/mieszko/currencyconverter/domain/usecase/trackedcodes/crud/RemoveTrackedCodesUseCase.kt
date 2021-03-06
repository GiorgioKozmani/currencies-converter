package com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud

import com.mieszko.currencyconverter.common.model.SupportedCode
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class RemoveTrackedCodesUseCase(
    private val getTrackedCodesOnceUseCase: IGetTrackedCodesOnceUseCase,
    private val saveTrackedCodesUseCase: ISaveTrackedCodesUseCase,
) : IRemoveTrackedCodesUseCase {

    override fun invoke(codeToRemove: SupportedCode): Completable =
        getTrackedCodesOnceUseCase()
            .flatMapCompletable { currentTrackedCodes ->
                //todo check thread
                //todo some errors would be nice
                Single.fromCallable {
                    currentTrackedCodes.let { currentList ->
                        if (currentList.contains(codeToRemove)) {
                            currentList.toMutableList().apply { remove(codeToRemove) }
                        } else {
                            currentTrackedCodes
                        }
                    }
                }
                    .subscribeOn(Schedulers.computation())
                    .flatMapCompletable { codes ->
                        saveTrackedCodesUseCase(codes)
                            .subscribeOn(Schedulers.io())
                    }
            }

}

interface IRemoveTrackedCodesUseCase {
    operator fun invoke(codeToRemove: SupportedCode): Completable
}
