package com.mieszko.currencyconverter.domain.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mieszko.currencyconverter.common.model.SupportedCode
import com.mieszko.currencyconverter.domain.analytics.base.FirebaseAnalyticsEvent
import com.mieszko.currencyconverter.domain.analytics.constants.AnalyticsConstants.UserProperties.BASE_CURRENCY

interface IFirebaseEventsLogger {
    fun logEvent(event: FirebaseAnalyticsEvent)
    fun logNonFatalError(throwable: Throwable, customMessage: String)

    // todo refactor so it's generic
    fun setBaseCurrencyUserProperty(baseCurrency: SupportedCode?)
}

class FirebaseEventsLogger(
    private val firebaseAnalytics: FirebaseAnalytics,
    private val firebaseCrashlytics: FirebaseCrashlytics
) : IFirebaseEventsLogger {

    override fun logEvent(event: FirebaseAnalyticsEvent) {
        firebaseAnalytics.logEvent(
            event.name,
            Bundle().apply { event.params.forEach { it.addToBundle(this) } }
        )
    }

    override fun logNonFatalError(throwable: Throwable, customMessage: String) {
        firebaseCrashlytics.run {
            log(customMessage)
            recordException(throwable)
        }
    }

    override fun setBaseCurrencyUserProperty(baseCurrency: SupportedCode?) {
        firebaseAnalytics.setUserProperty(BASE_CURRENCY, baseCurrency?.name)
    }
}
