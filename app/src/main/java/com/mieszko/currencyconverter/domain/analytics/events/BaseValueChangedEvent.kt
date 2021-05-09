package com.mieszko.currencyconverter.domain.analytics.events

import com.mieszko.currencyconverter.common.model.SupportedCode
import com.mieszko.currencyconverter.domain.analytics.base.FirebaseAnalyticsEvent
import com.mieszko.currencyconverter.domain.analytics.common.FirebaseParameter
import com.mieszko.currencyconverter.domain.analytics.constants.AnalyticsConstants

class BaseValueChangedEvent(
    baseCode: SupportedCode,
    baseValue: Double
) : FirebaseAnalyticsEvent(
    AnalyticsConstants.Events.BaseValueChanged.EVENT,
    listOf(
        FirebaseParameter.StringParameter(
            AnalyticsConstants.Events.CommonParams.CODE,
            baseCode.name
        ),
        FirebaseParameter.DoubleParameter(
            AnalyticsConstants.Events.CommonParams.VALUE,
            baseValue
        )
    )
)
