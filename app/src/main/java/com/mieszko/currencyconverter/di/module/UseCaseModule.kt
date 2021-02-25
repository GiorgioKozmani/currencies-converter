package com.mieszko.currencyconverter.di.module

import com.mieszko.currencyconverter.domain.usecase.trackedcodes.IMoveTrackedCodeToTopUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.ISwapTrackedCodesUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.MoveTrackedCodeToTopUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.SwapTrackedCodesUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud.*
import org.koin.dsl.module

/**
 * UseCase module
 *
 * It's important NOT TO MAKE SINGLE INSTANCES of UseCases
 */
val useCaseModule = module {

    factory<IAddTrackedCodesUseCase> {
        AddTrackedCodesUseCase(
            getTrackedCodesOnceUseCase = get(),
            saveTrackedCodesUseCase = get()
        )
    }

    factory<IGetTrackedCodesOnceUseCase> {
        GetTrackedCodesOnceUseCase(trackedCodesRepository = get())
    }

    factory<IObserveTrackedCodesUseCase> {
        ObserveTrackedCodesUseCase(trackedCodesRepository = get())
    }

    factory<IRemoveTrackedCodesUseCase> {
        RemoveTrackedCodesUseCase(
            getTrackedCodesOnceUseCase = get(),
            saveTrackedCodesUseCase = get()
        )
    }

    factory<ISaveTrackedCodesUseCase> {
        SaveTrackedCodesUseCase(trackedCodesRepository = get())
    }

    factory<IMoveTrackedCodeToTopUseCase> {
        MoveTrackedCodeToTopUseCase(
            getTrackedCodesOnceUseCase = get(),
            saveTrackedCodesUseCase = get()
        )
    }

    factory<ISwapTrackedCodesUseCase> {
        SwapTrackedCodesUseCase(
            getTrackedCodesOnceUseCase = get(),
            saveTrackedCodesUseCase = get()
        )
    }
}