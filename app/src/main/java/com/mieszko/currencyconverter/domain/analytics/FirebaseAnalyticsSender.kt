package com.mieszko.currencyconverter.domain.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.mieszko.currencyconverter.common.model.SupportedCode
import com.mieszko.currencyconverter.domain.analytics.base.FirebaseAnalyticsEvent

interface IFirebaseEventsLogger {
    fun logEvent(event: FirebaseAnalyticsEvent)

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

    override fun setBaseCurrencyUserProperty(baseCurrency: SupportedCode?) {
        firebaseAnalytics.setUserProperty("base_currency", baseCurrency?.name)
    }
}
