package com.mieszko.currencyconverter.di

import com.mieszko.currencyconverter.data.api.RetrofitService
import org.koin.dsl.module

val apiModule = module {
    single { RetrofitService().getCurrenciesApi() }
}
