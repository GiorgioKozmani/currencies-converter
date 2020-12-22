package com.mieszko.currencyconverter.di

import com.mieszko.currencyconverter.viewmodel.CurrenciesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { CurrenciesViewModel(get()) }
}