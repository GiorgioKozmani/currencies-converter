package com.mieszko.currencyconverter.di

import com.mieszko.currencyconverter.viewmodel.SelectionViewModel
import com.mieszko.currencyconverter.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel { SelectionViewModel() }
}