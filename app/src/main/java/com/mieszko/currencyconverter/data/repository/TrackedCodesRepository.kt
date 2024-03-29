package com.mieszko.currencyconverter.data.repository

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mieszko.currencyconverter.common.model.SupportedCode
import com.mieszko.currencyconverter.data.persistance.ISharedPrefsManager
import com.mieszko.currencyconverter.domain.repository.ITrackedCodesRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.lang.reflect.Type

class TrackedCodesRepository(
    private val sharedPrefsManager: ISharedPrefsManager,
    private val gson: Gson,
) : ITrackedCodesRepository {

    private val supportedCurrencyType: Type = object : TypeToken<List<SupportedCode>>() {}.type

    private val source: BehaviorSubject<List<SupportedCode>> =
        BehaviorSubject.createDefault(
            gson.fromJson(
                sharedPrefsManager.getString(
                    key = ISharedPrefsManager.Key.TrackedCurrencies,
                    // default to an empty array (no codes tracked)
                    defValue = "[]"
                ),
                supportedCurrencyType
            )
        )

    override fun observeTrackedCodes(): Observable<List<SupportedCode>> {
        return source
    }

    override fun saveTrackedCodes(trackedCodes: List<SupportedCode>): Completable =
        Completable.fromAction {
            sharedPrefsManager.put(
                ISharedPrefsManager.Key.TrackedCurrencies,
                gson.toJson(trackedCodes, supportedCurrencyType)
            )
            source.onNext(trackedCodes)
        }

    override fun getTrackedCodesOnce(): Single<List<SupportedCode>> =
        source.firstOrError()
}
