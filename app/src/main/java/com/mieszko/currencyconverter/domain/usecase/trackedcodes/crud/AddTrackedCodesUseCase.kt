package com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud

import com.mieszko.currencyconverter.common.model.SupportedCode
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

// todo add note that it's added to the end of the list javadoc
class AddTrackedCodesUseCase(
    private val getTrackedCodesOnceUseCase: IGetTrackedCodesOnceUseCase,
    private val saveTrackedCodesUseCase: ISaveTrackedCodesUseCase,
) : IAddTrackedCodesUseCase {

    override fun invoke(codeToAdd: SupportedCode): Completable =
        getTrackedCodesOnceUseCase()
            .flatMapCompletable { currentTrackedCodes ->
                // todo check thread
                // todo some errors would be nice
                Single.fromCallable {
                    currentTrackedCodes.let { currentList ->
                        if (!currentList.contains(codeToAdd)) {
                            currentList.toMutableList().apply { add(codeToAdd) }
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

interface IAddTrackedCodesUseCase {
    operator fun invoke(codeToAdd: SupportedCode): Completable
}
