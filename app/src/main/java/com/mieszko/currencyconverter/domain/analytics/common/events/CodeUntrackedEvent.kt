package com.mieszko.currencyconverter.domain.analytics.common.events

import com.mieszko.currencyconverter.common.model.SupportedCode
import com.mieszko.currencyconverter.domain.analytics.base.FirebaseAnalyticsEvent
import com.mieszko.currencyconverter.domain.analytics.common.params.FirebaseParameter
import com.mieszko.currencyconverter.domain.analytics.constants.AnalyticsConstants

class CodeUntrackedEvent(
    trackedCode: SupportedCode,
    currentSearchTerm: String
) : FirebaseAnalyticsEvent(
    AnalyticsConstants.Events.CodeUntracked.EVENT,
    listOf(
        FirebaseParameter.StringParameter(
            AnalyticsConstants.Events.CodeUntracked.Params.CODE,
            trackedCode.name
        ),
        FirebaseParameter.StringParameter(
            AnalyticsConstants.Events.CodeUntracked.Params.SEARCH_TERM,
            currentSearchTerm
        )
    )
)
