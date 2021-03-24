package com.mieszko.currencyconverter.domain.analytics.events

import com.mieszko.currencyconverter.common.model.SupportedCode
import com.mieszko.currencyconverter.domain.analytics.base.FirebaseAnalyticsEvent
import com.mieszko.currencyconverter.domain.analytics.common.FirebaseParameter
import com.mieszko.currencyconverter.domain.analytics.constants.AnalyticsConstants

class CodeTrackedEvent(
    trackedCode: SupportedCode,
    currentSearchTerm: String
) : FirebaseAnalyticsEvent(
    AnalyticsConstants.Events.CodeTracked.EVENT,
    listOf(
        FirebaseParameter.StringParameter(
            AnalyticsConstants.Events.CodeTracked.Params.CODE,
            trackedCode.name
        ),
        FirebaseParameter.StringParameter(
            AnalyticsConstants.Events.CodeTracked.Params.SEARCH_TERM,
            currentSearchTerm
        )
    )
)
