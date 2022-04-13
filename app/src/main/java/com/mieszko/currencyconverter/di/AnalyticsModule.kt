package com.mieszko.currencyconverter.di

import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.mieszko.currencyconverter.domain.analytics.FirebaseEventsLogger
import com.mieszko.currencyconverter.domain.analytics.IFirebaseEventsLogger
import org.koin.dsl.module

val analyticsModule = module {
    single<IFirebaseEventsLogger> {
        FirebaseEventsLogger(
            Firebase.analytics,
            Firebase.crashlytics
        )
    }
}
