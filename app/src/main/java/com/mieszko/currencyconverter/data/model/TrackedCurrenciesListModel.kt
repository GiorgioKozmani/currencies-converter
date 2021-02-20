package com.mieszko.currencyconverter.data.model

import com.mieszko.currencyconverter.common.CodeData
import com.mieszko.currencyconverter.common.SupportedCode

data class TrackedCurrenciesListModel(
    val code: SupportedCode,
    val codeData: CodeData,
)
