package com.mieszko.currencyconverter.domain.repository

import com.mieszko.currencyconverter.common.model.SupportedCode
import com.mieszko.currencyconverter.data.model.CodeData

interface ICodesDataRepository {
    fun getCodeStaticData(code: SupportedCode): CodeData?
}