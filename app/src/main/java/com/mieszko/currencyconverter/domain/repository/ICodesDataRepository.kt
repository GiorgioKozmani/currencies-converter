package com.mieszko.currencyconverter.domain.repository

import com.mieszko.currencyconverter.common.SupportedCode
import com.mieszko.currencyconverter.data.model.CodeData

interface ICodesDataRepository {
    fun getCodeData(code: SupportedCode): CodeData
}