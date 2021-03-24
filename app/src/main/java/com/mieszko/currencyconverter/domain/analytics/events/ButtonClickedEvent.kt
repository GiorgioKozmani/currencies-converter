package com.mieszko.currencyconverter.domain.analytics.events

import com.mieszko.currencyconverter.domain.analytics.base.FirebaseAnalyticsEvent
import com.mieszko.currencyconverter.domain.analytics.common.FirebaseParameter
import com.mieszko.currencyconverter.domain.analytics.constants.AnalyticsConstants

class ButtonClickedEvent(
    buttonName: String
) : FirebaseAnalyticsEvent(
    AnalyticsConstants.Events.ButtonClicked.EVENT,
    listOf(
        FirebaseParameter.StringParameter(
            AnalyticsConstants.Events.ButtonClicked.Params.NAME,
            buttonName
        )
    )
)
