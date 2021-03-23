package com.mieszko.currencyconverter.di.module

import com.mieszko.currencyconverter.data.repository.CodesDataRepository
import com.mieszko.currencyconverter.data.repository.RatiosRepository
import com.mieszko.currencyconverter.data.repository.TrackedCodesRepository
import com.mieszko.currencyconverter.domain.repository.ICodesDataRepository
import com.mieszko.currencyconverter.domain.repository.IRatiosRepository
import com.mieszko.currencyconverter.domain.repository.ITrackedCodesRepository
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
    single<ITrackedCodesRepository> {
        TrackedCodesRepository(sharedPrefsManager = get())
    }
    single<ICodesDataRepository> {
        CodesDataRepository()
    }
}
