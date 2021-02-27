package com.mieszko.currencyconverter.di.module

import com.mieszko.currencyconverter.domain.usecase.IMapDataToCodesUseCase
import com.mieszko.currencyconverter.domain.usecase.MapDataToCodesUseCase
import com.mieszko.currencyconverter.domain.usecase.ratios.FetchRemoteRatiosUseCase
import com.mieszko.currencyconverter.domain.usecase.ratios.IFetchRemoteRatiosUseCase
import com.mieszko.currencyconverter.domain.usecase.ratios.IObserveRatiosUseCase
import com.mieszko.currencyconverter.domain.usecase.ratios.ObserveRatiosUseCase
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

    /**
     * -----------------------
     * TRACKED CODES USECASES
     * -----------------------
     */

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

    /**
     * -----------------------
     * RATIOS USECASES
     * -----------------------
     */

    factory<IObserveRatiosUseCase> {
        ObserveRatiosUseCase(ratiosRepository = get())
    }

    factory<IFetchRemoteRatiosUseCase> {
        FetchRemoteRatiosUseCase(ratiosRepository = get())
    }

    /**
     * -----------------------
     * OTHER USECASES
     * -----------------------
     */

    factory<IMapDataToCodesUseCase> {
        MapDataToCodesUseCase(codesDataRepository = get())
    }
}