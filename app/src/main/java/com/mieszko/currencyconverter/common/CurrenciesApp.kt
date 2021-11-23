package com.mieszko.currencyconverter.common

import android.app.Application
import android.content.res.Resources
import com.mieszko.currencyconverter.di.module.analyticsModule
import com.mieszko.currencyconverter.di.module.apiModule
import com.mieszko.currencyconverter.di.module.cacheModule
import com.mieszko.currencyconverter.di.module.gsonModule
import com.mieszko.currencyconverter.di.module.repoModule
import com.mieszko.currencyconverter.di.module.sharedPrefsModule
import com.mieszko.currencyconverter.di.module.useCaseModule
import com.mieszko.currencyconverter.di.module.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class CurrenciesApp : Application() {

    override fun onCreate() {
        super.onCreate()
        resourses = resources

        startKoin {
            androidLogger()
            androidContext(this@CurrenciesApp)
            modules(
                listOf(
                    gsonModule,
                    repoModule,
                    apiModule,
                    cacheModule,
                    viewModelModule,
                    sharedPrefsModule,
                    useCaseModule,
                    analyticsModule
                )
            )
        }
    }

    companion object {
        lateinit var resourses: Resources
    }
}
