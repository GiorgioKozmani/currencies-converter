package com.mieszko.currencyconverter.di.module

import com.mieszko.currencyconverter.data.persistance.RatiosCache
import com.mieszko.currencyconverter.data.persistance.IRatiosCache
import org.koin.dsl.module

val cacheModule = module {
    single<IRatiosCache> { RatiosCache(get()) }
}