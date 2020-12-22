package com.mieszko

import android.app.Application
import com.mieszko.currencyconverter.util.apiModule
import com.mieszko.currencyconverter.util.cacheModule
import com.mieszko.currencyconverter.data.persistance.SharedPrefs
import com.mieszko.currencyconverter.util.repoModule
import com.mieszko.currencyconverter.util.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CurrenciesApp : Application() {

    override fun onCreate() {
        super.onCreate()
        SharedPrefs.init(this)
        startKoin {
            androidContext(this@CurrenciesApp)
            modules(listOf(
                repoModule,
                apiModule,
                cacheModule,
                viewModelModule
            ))
        }
    }

}
