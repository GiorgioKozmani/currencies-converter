package com.mieszko.currencyconverter.domain.model.list

import com.mieszko.currencyconverter.common.model.SupportedCode
import com.mieszko.currencyconverter.data.model.CodeData

data class AllCurrenciesListModel(
    val code: SupportedCode,
    val codeData: CodeData,
    val isTracked: Boolean
)
