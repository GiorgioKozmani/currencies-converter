package com.mieszko.currencyconverter.di.module

import com.mieszko.currencyconverter.data.persistance.ISharedPrefsManager
import com.mieszko.currencyconverter.data.persistance.SharedPrefsManager
import org.koin.dsl.module

val sharedPrefsModule = module {
    single<ISharedPrefsManager> {
        SharedPrefsManager(context = get())
    }
}
