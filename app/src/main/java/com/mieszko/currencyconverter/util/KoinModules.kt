package com.mieszko.currencyconverter.util

import com.mieszko.currencyconverter.data.util.RetrofitService
import com.mieszko.currencyconverter.viewmodel.CurrenciesViewModel
import com.mieszko.currencyconverter.data.persistance.CurrenciesCache
import com.mieszko.currencyconverter.data.persistance.ICurrenciesCache
import com.mieszko.currencyconverter.data.repository.CurrenciesRepository
import com.mieszko.currencyconverter.data.repository.ICurrenciesRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val repoModule = module {
    single<ICurrenciesRepository> {
        CurrenciesRepository(
            currenciesApi = get(),
            cache = get()
        )
    }
}

val viewModelModule = module {
    viewModel { CurrenciesViewModel(get()) }
}

val apiModule = module {
    single { RetrofitService().getCurrenciesApi() }
}

val cacheModule = module {
    single<ICurrenciesCache> { CurrenciesCache() }
}