package com.mieszko.currencyconverter.domain.repository

import com.mieszko.currencyconverter.common.model.SupportedCode
import com.mieszko.currencyconverter.data.model.CodeStaticData

interface ICodesStaticDataRepository {
    fun getCodeStaticData(code: SupportedCode): CodeStaticData?
}
