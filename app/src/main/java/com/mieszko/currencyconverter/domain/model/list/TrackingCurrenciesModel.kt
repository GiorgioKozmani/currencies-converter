package com.mieszko.currencyconverter.domain.model.list

import com.mieszko.currencyconverter.common.model.SupportedCode
import com.mieszko.currencyconverter.data.model.CodeStaticData

data class TrackingCurrenciesModel(
    val code: SupportedCode,
    val codeStaticData: CodeStaticData,
    val isTracked: Boolean
)
