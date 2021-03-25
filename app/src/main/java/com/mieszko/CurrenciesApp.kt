package com.mieszko

import android.app.Application
import android.content.res.Resources
import com.mieszko.currencyconverter.di.module.analyticsModule
import com.mieszko.currencyconverter.di.module.apiModule
import com.mieszko.currencyconverter.di.module.cacheModule
import com.mieszko.currencyconverter.di.module.repoModule
import com.mieszko.currencyconverter.di.module.sharedPrefsModule
import com.mieszko.currencyconverter.di.module.useCaseModule
import com.mieszko.currencyconverter.di.module.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class CurrenciesApp : Application() {

    //TODO ADD
    // - IF IT'S FIRST RUN < EFFECTIVELY ALSO AFTER DATA CLEAR >
    // TODO HANDLE THIS INSIDE OF THE REPOS, NOT HERE!
    // a) set selected currencies to something (for example euro, dollar, gbp, chinese)
    // b) if ratios is empty load ratios from static file (remember to include date)

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
