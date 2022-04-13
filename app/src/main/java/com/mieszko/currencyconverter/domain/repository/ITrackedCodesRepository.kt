package com.mieszko.currencyconverter.domain.repository

import com.mieszko.currencyconverter.common.model.SupportedCode
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface ITrackedCodesRepository {
    fun saveTrackedCodes(trackedCodes: List<SupportedCode>): Completable

    fun observeTrackedCodes(): Observable<List<SupportedCode>>
}
