package com.mieszko.currencyconverter.domain.repository

import com.mieszko.currencyconverter.common.SupportedCode
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface ITrackedCurrenciesRepository {
    //todo javadoc
    fun getTrackedCurrencies(): Observable<List<SupportedCode>>

    //todo add note that it's added to the end of the list
    fun addTrackedCurrency(trackedCurrency: SupportedCode): Completable

    fun removeTrackedCurrency(untrackedCurrency: SupportedCode): Completable

    fun swapTrackingOrder(
        firstCurrency: SupportedCode,
        secondCurrency: SupportedCode
    ): Completable

    fun moveTrackedCurrencyToTop(trackedCurrency: SupportedCode): Completable
}