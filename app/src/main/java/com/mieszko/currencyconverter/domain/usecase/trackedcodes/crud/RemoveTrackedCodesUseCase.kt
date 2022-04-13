package com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud

import com.mieszko.currencyconverter.common.model.SupportedCode
import com.mieszko.currencyconverter.domain.repository.ITrackedCodesRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class RemoveTrackedCodesUseCase(
    private val trackedCodesRepository: ITrackedCodesRepository,
    private val saveTrackedCodesUseCase: ISaveTrackedCodesUseCase,
) : IRemoveTrackedCodesUseCase {

    override fun invoke(codeToRemove: SupportedCode): Completable =
        trackedCodesRepository.observeTrackedCodes()
            .firstOrError()
            .observeOn(Schedulers.io())
            .flatMapCompletable { currentTrackedCodes ->
                Single.fromCallable {
                    currentTrackedCodes.let { currentList ->
                        if (currentList.contains(codeToRemove)) {
                            currentList.toMutableList().apply { remove(codeToRemove) }
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

interface IRemoveTrackedCodesUseCase {
    operator fun invoke(codeToRemove: SupportedCode): Completable
}
