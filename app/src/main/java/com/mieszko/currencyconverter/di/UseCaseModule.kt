package com.mieszko.currencyconverter.di

import com.mieszko.currencyconverter.domain.usecase.mappers.IMapDataToCodesUseCase
import com.mieszko.currencyconverter.domain.usecase.mappers.MapDataToCodesUseCase
import com.mieszko.currencyconverter.domain.usecase.ratios.IMakeRatioStringUseCase
import com.mieszko.currencyconverter.domain.usecase.ratios.MakeRatioStringUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.CreateTrackedCodesModelsUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.ICreateTrackedCodesModelsUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.IMoveTrackedCodeToTopUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.ISwapTrackedCodesUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.MoveTrackedCodeToTopUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.SwapTrackedCodesUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud.AddTrackedCodesUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud.IAddTrackedCodesUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud.IRemoveTrackedCodesUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud.ISaveTrackedCodesUseCase
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
            trackedCodesRepository = get(),
            saveTrackedCodesUseCase = get()
        )
    }

    factory<IRemoveTrackedCodesUseCase> {
        RemoveTrackedCodesUseCase(
            trackedCodesRepository = get(),
            saveTrackedCodesUseCase = get()
        )
    }

    factory<ISaveTrackedCodesUseCase> {
        SaveTrackedCodesUseCase(trackedCodesRepository = get(), get())
    }

    factory<IMoveTrackedCodeToTopUseCase> {
        MoveTrackedCodeToTopUseCase(
            trackedCodesRepository = get(),
            saveTrackedCodesUseCase = get()
        )
    }

    factory<ISwapTrackedCodesUseCase> {
        SwapTrackedCodesUseCase(
            trackedCodesRepository = get(),
            saveTrackedCodesUseCase = get()
        )
    }

    factory<ICreateTrackedCodesModelsUseCase> {
        CreateTrackedCodesModelsUseCase(
            codesStaticDataRepository = get()
        )
    }

    /**
     * -----------------------
     * RATIOS USECASES
     * -----------------------
     */

    single<IMakeRatioStringUseCase> {
        MakeRatioStringUseCase()
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
