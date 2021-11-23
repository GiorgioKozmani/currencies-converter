package com.mieszko.currencyconverter.domain.analytics.events

import com.mieszko.currencyconverter.domain.analytics.base.FirebaseAnalyticsEvent
import com.mieszko.currencyconverter.domain.analytics.common.FirebaseParameter
import com.mieszko.currencyconverter.domain.analytics.constants.AnalyticsConstants

class MoreTabItemClickedEvent(
    moreItem: AnalyticsConstants.Events.MoreTabItemClicked.MoreItem
) : FirebaseAnalyticsEvent(
    AnalyticsConstants.Events.MoreTabItemClicked.EVENT,
    listOf(
        FirebaseParameter.StringParameter(
            AnalyticsConstants.Events.CommonParams.NAME,
            moreItem.name
        )
    )
)
