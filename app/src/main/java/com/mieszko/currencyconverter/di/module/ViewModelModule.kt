package com.mieszko.currencyconverter.di.module

import com.mieszko.currencyconverter.common.util.DisposablesBag
import com.mieszko.currencyconverter.common.util.IDisposablesBag
import com.mieszko.currencyconverter.presentation.home.HomeViewModel
import com.mieszko.currencyconverter.presentation.selection.SelectionViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        HomeViewModel(
            disposablesBag = get(),
            fetchRemoteRatiosUseCase = get(),
            mapCodeToDataUseCase = get(),
            moveTrackedCodeToTopUseCase = get(),
            observeTrackedCodesUseCase = get(),
            swapTrackedCodesUseCase = get(),
            observeRatiosUseCase = get()
        )
    }
    viewModel {
        SelectionViewModel(
            disposablesBag = get(),
            observeTrackedCodesUseCase = get(),
            codesDataRepository = get(),
            addTrackedCodesUseCase = get(),
            removeTrackedCodesUseCase = get()
        )
    }
    factory<IDisposablesBag> { DisposablesBag(CompositeDisposable()) }
}
