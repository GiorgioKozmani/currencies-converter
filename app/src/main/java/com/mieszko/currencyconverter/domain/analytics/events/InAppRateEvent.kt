package com.mieszko.currencyconverter.domain.analytics.events

import com.mieszko.currencyconverter.domain.analytics.base.FirebaseAnalyticsEvent
import com.mieszko.currencyconverter.domain.analytics.common.FirebaseParameter
import com.mieszko.currencyconverter.domain.analytics.constants.AnalyticsConstants

class InAppRateEvent(
    starsNumber: Int
) : FirebaseAnalyticsEvent(
    AnalyticsConstants.Events.InAppRate.EVENT,
    listOf(
        FirebaseParameter.IntParameter(
            AnalyticsConstants.Events.CommonParams.VALUE,
            starsNumber
        )
    )
)
