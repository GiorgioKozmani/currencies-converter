package com.mieszko.currencyconverter.domain.model

import com.mieszko.currencyconverter.common.SupportedCode
import com.mieszko.currencyconverter.data.model.CodeData

data class CodeWithData(val code: SupportedCode, val data: CodeData)