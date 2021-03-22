package com.mieszko.currencyconverter.domain.analytics.base

import com.mieszko.currencyconverter.domain.analytics.common.params.FirebaseParameter

abstract class FirebaseAnalyticsEvent(
    val name: String,
    val params: List<FirebaseParameter>
)

