package com.mieszko.currencyconverter.data.model

import com.mieszko.currencyconverter.common.SupportedCurrency

//TODO THIS SHOULD CONTAIN ONLY CURRENCY AND STRINGS? SO THERE'S 0 LOGIC DOWN IN VH / ADAPTER


sealed class HomeListItem {
    abstract val currency: SupportedCurrency
    abstract val amount: Double

    data class Base(
        override val currency: SupportedCurrency,
        override val amount: Double,
    ) : HomeListItem()

    //todo rethink mutability
    data class Regular(
        override val currency: SupportedCurrency,
        override val amount: Double,
        val baseToThisText: String,
        val thisToBaseText: String
    ) : HomeListItem()
}