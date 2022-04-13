package com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud

import com.mieszko.currencyconverter.common.model.SupportedCode
import com.mieszko.currencyconverter.domain.repository.ITrackedCodesRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class AddTrackedCodesUseCase(
    private val trackedCodesRepository: ITrackedCodesRepository,
    private val saveTrackedCodesUseCase: ISaveTrackedCodesUseCase,
) : IAddTrackedCodesUseCase {

    override fun invoke(codeToAdd: SupportedCode): Completable =
        trackedCodesRepository
            .observeTrackedCodes()
            .firstOrError()
            .observeOn(Schedulers.io())
            .flatMapCompletable { currentTrackedCodes ->
                Single.fromCallable {
                    currentTrackedCodes.let { currentList ->
                        if (!currentList.contains(codeToAdd)) {
                            currentList.toMutableList().apply { add(codeToAdd) }
                        } else {
                            currentTrackedCodes
                        }
                    }
                }
                    .flatMapCompletable { codes ->
                        saveTrackedCodesUseCase(codes)
                    }
            }
}

interface IAddTrackedCodesUseCase {
    operator fun invoke(codeToAdd: SupportedCode): Completable
}
