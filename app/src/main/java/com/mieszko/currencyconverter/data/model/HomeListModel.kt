package com.mieszko.currencyconverter.data.model

import com.mieszko.currencyconverter.common.CodeData
import com.mieszko.currencyconverter.common.SupportedCode

sealed class HomeListModel {
    //todo idea, if code and code data always go together in model, it might be worth to revert supported currency class combiding these!
    abstract val code: SupportedCode
    abstract val codeData: CodeData
    abstract val amount: Double

    data class Base(
        override val code: SupportedCode,
        override val codeData: CodeData,
        override val amount: Double,
    ) : HomeListModel()

    data class Regular(
        override val code: SupportedCode,
        override val codeData: CodeData,
        override val amount: Double,
        val baseToThisText: String,
        val thisToBaseText: String
    ) : HomeListModel()
}