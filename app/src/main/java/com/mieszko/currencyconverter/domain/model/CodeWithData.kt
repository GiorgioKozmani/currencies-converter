package com.mieszko.currencyconverter.domain.model

import com.mieszko.currencyconverter.common.model.SupportedCode
import com.mieszko.currencyconverter.data.model.CodeData

data class CodeWithData(val code: SupportedCode, val toUahRatio: Double, val data: CodeData)
