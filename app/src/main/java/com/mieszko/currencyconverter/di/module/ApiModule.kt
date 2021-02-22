package com.mieszko.currencyconverter.di.module

import com.mieszko.currencyconverter.data.util.RetrofitService
import org.koin.dsl.module

val apiModule = module {
    single { RetrofitService().getCurrenciesApi() }
}
