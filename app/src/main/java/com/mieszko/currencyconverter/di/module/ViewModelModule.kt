package com.mieszko.currencyconverter.di.module

import com.mieszko.currencyconverter.common.DisposablesBag
import com.mieszko.currencyconverter.common.IDisposablesBag
import com.mieszko.currencyconverter.presentation.home.HomeViewModel
import com.mieszko.currencyconverter.presentation.tracking.TrackingViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        HomeViewModel(
            disposablesBag = get(),
            codesDataRepository = get(),
            ratiosRepository = get(),
            moveTrackedCodeToTopUseCase = get(),
            observeTrackedCodesUseCase = get(),
            swapTrackedCodesUseCase = get()
        )
    }
    viewModel {
        TrackingViewModel(
            disposablesBag = get(),
            observeTrackedCodesUseCase = get(),
            codesDataRepository = get(),
            addTrackedCodesUseCase = get(),
            removeTrackedCodesUseCase = get()
        )
    }
    factory<IDisposablesBag> { DisposablesBag(CompositeDisposable()) }
}