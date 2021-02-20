package com.mieszko.currencyconverter.di

import com.mieszko.currencyconverter.data.repository.*
import org.koin.dsl.module

/**
 * Repo module
 *
 * It's important to keep only single instances of repositories so they can act as Single Source Of Truths
 */
val repoModule = module {
    single<IRatiosRepository> {
        RatiosRepository(currenciesApi = get(), cache = get())
    }
    single<ITrackedCurrenciesRepository> {
        TrackedCodesRepository(sharedPrefsManager = get())
    }
    single<ICodesDataRepository> {
        CodesDataRepository()
    }
}