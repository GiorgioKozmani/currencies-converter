package com.mieszko.currencyconverter.domain.model.list

import com.mieszko.currencyconverter.common.model.SupportedCode
import com.mieszko.currencyconverter.data.model.CodeStaticData

sealed class HomeListModel {
    // todo idea, if code and code data always go together in model, it might be worth to revert supported currency class combiding these!
    abstract val code: SupportedCode
    abstract val codeStaticData: CodeStaticData
    abstract val amount: Double

    data class Base(
        override val code: SupportedCode,
        override val codeStaticData: CodeStaticData,
        override val amount: Double,
    ) : HomeListModel()

    data class NonBase(
        override val code: SupportedCode,
        override val codeStaticData: CodeStaticData,
        override val amount: Double,
        val baseToThisText: String,
        val thisToBaseText: String
    ) : HomeListModel()
}
