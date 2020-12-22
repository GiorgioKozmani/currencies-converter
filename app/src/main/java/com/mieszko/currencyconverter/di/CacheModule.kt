package com.mieszko.currencyconverter.di

import com.mieszko.currencyconverter.data.persistance.CurrenciesCache
import com.mieszko.currencyconverter.data.persistance.ICurrenciesCache
import org.koin.dsl.module

val cacheModule = module {
    single<ICurrenciesCache> { CurrenciesCache() }
}