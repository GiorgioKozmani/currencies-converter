package com.mieszko.currencyconverter.di

import com.mieszko.currencyconverter.data.repository.CurrenciesRepository
import com.mieszko.currencyconverter.data.repository.ICurrenciesRepository
import org.koin.dsl.module

val repoModule = module {
    single<ICurrenciesRepository> {
        CurrenciesRepository(
            currenciesApi = get(),
            cache = get()
        )
    }
}