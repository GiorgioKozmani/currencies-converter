package com.mieszko

import android.app.Application
import android.content.res.Resources
import com.mieszko.currencyconverter.data.persistance.SharedPrefs
import com.mieszko.currencyconverter.di.apiModule
import com.mieszko.currencyconverter.di.cacheModule
import com.mieszko.currencyconverter.di.repoModule
import com.mieszko.currencyconverter.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class CurrenciesApp : Application() {

    override fun onCreate() {
        super.onCreate()
        resourses = resources

        SharedPrefs.init(this)
        startKoin {
            androidLogger()
            androidContext(this@CurrenciesApp)
            modules(
                listOf(
                    repoModule,
                    apiModule,
                    cacheModule,
                    viewModelModule
                )
            )
        }
    }

    companion object {
        lateinit var resourses: Resources
    }
}
