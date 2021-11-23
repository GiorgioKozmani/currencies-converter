package com.mieszko.currencyconverter.di.module

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
            disposablesBag = get(),
            fetchRemoteRatios = get(),
            mapCodesToData = get(),
            moveTrackedCodeToTop = get(),
            observeTrackedCodes = get(),
            swapTrackedCodes = get(),
            makeRatioString = get(),
            observeRatios = get(),
            eventsLogger = get()
        )
    }
    viewModel {
        SelectionViewModel(
            disposablesBag = get(),
            observeTrackedCodes = get(),
            createTrackedCodesModels = get(),
            addTrackedCodes = get(),
            removeTrackedCodes = get(),
            eventsLogger = get()
        )
    }
    factory<IDisposablesBag> { DisposablesBag(CompositeDisposable()) }
}
