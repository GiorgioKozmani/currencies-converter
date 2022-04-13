package com.mieszko.currencyconverter.di

import com.mieszko.currencyconverter.common.model.DisposablesBag
import com.mieszko.currencyconverter.common.model.IDisposablesBag
import com.mieszko.currencyconverter.presentation.home.HomeViewModel
import com.mieszko.currencyconverter.presentation.selection.SelectionViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        HomeViewModel(
            trackedCodesRepository = get(),
            ratiosRepository = get(),
            disposablesBag = get(),
            mapCodesToData = get(),
            moveTrackedCodeToTop = get(),
            swapTrackedCodes = get(),
            makeRatioString = get(),
            eventsLogger = get()
        )
    }
    viewModel {
        SelectionViewModel(
            disposablesBag = get(),
            trackedCodesRepository = get(),
            createTrackedCodesModels = get(),
            addTrackedCodes = get(),
            removeTrackedCodes = get(),
            eventsLogger = get()
        )
    }
    factory<IDisposablesBag> { DisposablesBag(CompositeDisposable()) }
}
