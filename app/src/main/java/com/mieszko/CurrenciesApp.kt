package com.mieszko

import android.app.Application
import android.content.res.Resources
import com.mieszko.currencyconverter.di.module.*
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
