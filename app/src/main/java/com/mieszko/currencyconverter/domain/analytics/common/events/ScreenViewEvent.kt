package com.mieszko.currencyconverter.domain.analytics.common.events

import com.google.firebase.analytics.FirebaseAnalytics
import com.mieszko.currencyconverter.domain.analytics.base.FirebaseAnalyticsEvent
import com.mieszko.currencyconverter.domain.analytics.common.params.FirebaseParameter
import com.mieszko.currencyconverter.domain.analytics.constants.AnalyticsConstants

class ScreenViewEvent(
    screen: AnalyticsConstants.Events.ScreenView.Screen
) : FirebaseAnalyticsEvent(
    AnalyticsConstants.Events.ScreenView.EVENT,
    listOf(FirebaseParameter.StringParameter(FirebaseAnalytics.Param.SCREEN_NAME, screen.name))
)
