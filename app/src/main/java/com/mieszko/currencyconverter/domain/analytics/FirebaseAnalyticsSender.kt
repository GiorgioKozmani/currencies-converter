package com.mieszko.currencyconverter.domain.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mieszko.currencyconverter.common.model.SupportedCode
import com.mieszko.currencyconverter.domain.analytics.base.FirebaseAnalyticsEvent

interface IFirebaseEventsLogger {
    fun logEvent(event: FirebaseAnalyticsEvent)
    fun logError(throwable: Throwable, customMessage: String)

    // todo refactor so it's generic
    fun setBaseCurrencyUserProperty(baseCurrency: SupportedCode?)
}

class FirebaseAnalyticsSender(
    private val firebaseAnalytics: FirebaseAnalytics
) : IFirebaseEventsLogger {

    override fun logEvent(event: FirebaseAnalyticsEvent) {
        firebaseAnalytics.logEvent(
            event.name,
            Bundle().apply { event.params.forEach { it.addToBundle(this) } }
        )
    }

    override fun logError(throwable: Throwable, customMessage: String) {
        FirebaseCrashlytics.getInstance().run {
            log(customMessage)
            recordException(throwable)
        }

    }

    override fun setBaseCurrencyUserProperty(baseCurrency: SupportedCode?) {
        firebaseAnalytics.setUserProperty("base_currency", baseCurrency?.name)
    }
}
