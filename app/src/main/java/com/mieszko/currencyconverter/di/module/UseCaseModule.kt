package com.mieszko.currencyconverter.di.module

import com.mieszko.currencyconverter.domain.usecase.mappers.IMapDataToCodesUseCase
import com.mieszko.currencyconverter.domain.usecase.mappers.MapDataToCodesUseCase
import com.mieszko.currencyconverter.domain.usecase.ratios.FetchRemoteRatiosUseCase
import com.mieszko.currencyconverter.domain.usecase.ratios.IFetchRemoteRatiosUseCase
import com.mieszko.currencyconverter.domain.usecase.ratios.IObserveRatiosUseCase
import com.mieszko.currencyconverter.domain.usecase.ratios.ObserveRatiosUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.IMoveTrackedCodeToTopUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.ISwapTrackedCodesUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.MoveTrackedCodeToTopUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.SwapTrackedCodesUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud.AddTrackedCodesUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud.GetTrackedCodesOnceUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud.IAddTrackedCodesUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud.IGetTrackedCodesOnceUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud.IObserveTrackedCodesUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud.IRemoveTrackedCodesUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud.ISaveTrackedCodesUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud.ObserveTrackedCodesUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud.RemoveTrackedCodesUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud.SaveTrackedCodesUseCase
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
        SaveTrackedCodesUseCase(trackedCodesRepository = get(), get())
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
        ObserveRatiosUseCase(ratiosRepository = get(), eventsLogger = get())
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
        MapDataToCodesUseCase(codesStaticDataRepository = get())
    }
}
