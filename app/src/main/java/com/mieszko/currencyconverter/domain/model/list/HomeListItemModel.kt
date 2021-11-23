package com.mieszko.currencyconverter.domain.model.list

import com.mieszko.currencyconverter.common.model.SupportedCode
import com.mieszko.currencyconverter.data.model.CodeStaticData

sealed class HomeListItemModel {
    // todo [IDEA], if code and code data always go together in model,
    // it might be worth to combine these into one model.
    abstract val code: SupportedCode
    abstract val codeStaticData: CodeStaticData
    abstract val amount: Double

    data class Base(
        override val code: SupportedCode,
        override val codeStaticData: CodeStaticData,
        override val amount: Double,
    ) : HomeListItemModel()

    data class NonBase(
        override val code: SupportedCode,
        override val codeStaticData: CodeStaticData,
        override val amount: Double,
        val baseToThisText: String,
        val thisToBaseText: String
    ) : HomeListItemModel()
}
