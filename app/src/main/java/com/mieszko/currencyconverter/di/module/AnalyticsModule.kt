package com.mieszko.currencyconverter.di.module

import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.mieszko.currencyconverter.domain.analytics.FirebaseAnalyticsSender
import com.mieszko.currencyconverter.domain.analytics.IFirebaseEventsLogger
import org.koin.dsl.module

val analyticsModule = module {
    single<IFirebaseEventsLogger> { FirebaseAnalyticsSender(Firebase.analytics) }
}
