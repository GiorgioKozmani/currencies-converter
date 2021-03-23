package com.mieszko.currencyconverter.domain.analytics.common.events

import com.mieszko.currencyconverter.domain.analytics.base.FirebaseAnalyticsEvent
import com.mieszko.currencyconverter.domain.analytics.common.params.FirebaseParameter
import com.mieszko.currencyconverter.domain.analytics.constants.AnalyticsConstants

class SearchTermEvent(
    searchTerm: String
) : FirebaseAnalyticsEvent(
    AnalyticsConstants.Events.SearchTerm.EVENT,
    listOf(
        FirebaseParameter.StringParameter(
            AnalyticsConstants.Events.SearchTerm.Params.SEARCH_TERM,
            searchTerm
        )
    )
)
