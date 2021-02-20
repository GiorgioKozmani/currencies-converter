package com.mieszko.currencyconverter.di

import com.mieszko.currencyconverter.viewmodel.HomeViewModel
import com.mieszko.currencyconverter.viewmodel.SelectionViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { SelectionViewModel(get(), get()) }
}