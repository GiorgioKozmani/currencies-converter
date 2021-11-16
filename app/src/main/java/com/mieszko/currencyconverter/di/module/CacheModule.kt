package com.mieszko.currencyconverter.di.module

import com.mieszko.currencyconverter.data.persistance.IRatiosCache
import com.mieszko.currencyconverter.data.persistance.RatiosCache
import org.koin.dsl.module

val cacheModule = module {
    single<IRatiosCache> { RatiosCache(get(), get()) }
}
