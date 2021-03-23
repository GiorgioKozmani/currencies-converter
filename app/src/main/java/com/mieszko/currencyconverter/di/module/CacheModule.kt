package com.mieszko.currencyconverter.di.module

import com.mieszko.currencyconverter.data.persistance.cache.ratios.IRatiosCache
import com.mieszko.currencyconverter.data.persistance.cache.ratios.RatiosCache
import org.koin.dsl.module

val cacheModule = module {
    single<IRatiosCache> { RatiosCache(get()) }
}
