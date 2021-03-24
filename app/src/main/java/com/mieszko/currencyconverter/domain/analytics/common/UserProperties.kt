package com.mieszko.currencyconverter.domain.analytics.common

import com.mieszko.currencyconverter.common.model.SupportedCode
import com.mieszko.currencyconverter.domain.analytics.base.AnalyticsProperty
import com.mieszko.currencyconverter.domain.analytics.constants.AnalyticsConstants

object UserProperties {
    class BaseCurrencyProperty(
        currency: SupportedCode?
    ) : AnalyticsProperty(AnalyticsConstants.UserProperties.BASE_CURRENCY, currency)
}