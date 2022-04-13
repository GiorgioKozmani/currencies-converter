package com.mieszko.currencyconverter

import android.app.Application
import android.content.res.Resources
import com.mieszko.currencyconverter.di.analyticsModule
import com.mieszko.currencyconverter.di.apiModule
import com.mieszko.currencyconverter.di.cacheModule
import com.mieszko.currencyconverter.di.gsonModule
import com.mieszko.currencyconverter.di.repoModule
import com.mieszko.currencyconverter.di.sharedPrefsModule
import com.mieszko.currencyconverter.di.useCaseModule
import com.mieszko.currencyconverter.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class CurrenciesApp : Application() {

    override fun onCreate() {
        super.onCreate()
        resourses = resources

        startKoin {
            androidLogger(Level.ERROR)
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
