package com.mieszko.currencyconverter.di.module

import com.mieszko.currencyconverter.data.persistance.cache.ratios.RatiosCache
import com.mieszko.currencyconverter.data.persistance.cache.ratios.IRatiosCache
import org.koin.dsl.module

val cacheModule = module {
    single<IRatiosCache> { RatiosCache(get()) }
}