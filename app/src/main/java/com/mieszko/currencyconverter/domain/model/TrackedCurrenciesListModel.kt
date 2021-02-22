package com.mieszko.currencyconverter.domain.model

import com.mieszko.currencyconverter.common.SupportedCode
import com.mieszko.currencyconverter.data.model.CodeData

data class TrackedCurrenciesListModel(
    val code: SupportedCode,
    val codeData: CodeData,
)
