package com.mieszko.currencyconverter.domain.model

import com.mieszko.currencyconverter.common.model.SupportedCode
import com.mieszko.currencyconverter.data.model.CodeStaticData

data class CodeWithData(
    val code: SupportedCode,
    val toUahRatio: Double,
    val staticData: CodeStaticData
)
