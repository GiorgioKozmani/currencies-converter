package com.mieszko.currencyconverter.domain.repository

import com.mieszko.currencyconverter.common.model.SupportedCode
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

interface ITrackedCodesRepository {
    // todo javadoc
    fun saveTrackedCodes(trackedCodes: List<SupportedCode>): Completable

    fun getTrackedCodesOnce(): Single<List<SupportedCode>>

    fun observeTrackedCodes(): Observable<List<SupportedCode>>
}
