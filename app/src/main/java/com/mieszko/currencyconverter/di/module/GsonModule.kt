package com.mieszko.currencyconverter.di.module

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.koin.dsl.module

val gsonModule = module {
    single<Gson> {
        GsonBuilder().create()
    }
}
