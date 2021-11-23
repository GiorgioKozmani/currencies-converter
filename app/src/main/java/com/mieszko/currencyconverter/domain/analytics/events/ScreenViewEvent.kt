package com.mieszko.currencyconverter.domain.analytics.events

import com.mieszko.currencyconverter.domain.analytics.base.FirebaseAnalyticsEvent
import com.mieszko.currencyconverter.domain.analytics.common.FirebaseParameter
import com.mieszko.currencyconverter.domain.analytics.constants.AnalyticsConstants

class ScreenViewEvent(
    screen: AnalyticsConstants.Events.ScreenView.Screen
) : FirebaseAnalyticsEvent(
    AnalyticsConstants.Events.ScreenView.EVENT,
    listOf(
        FirebaseParameter.StringParameter(
            AnalyticsConstants.Events.ScreenView.Params.SCREEN_NAME,
            screen.name
        )
    )
)
