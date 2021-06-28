package com.mieszko.currencyconverter.data.persistance.cache.ratios

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mieszko.currencyconverter.data.fallback.FallbackRatios
import com.mieszko.currencyconverter.data.model.CurrencyRatioDTO
import com.mieszko.currencyconverter.data.persistance.ISharedPrefsManager
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.lang.reflect.Type
import java.util.Date

class RatiosCache(private val sharedPrefsManager: ISharedPrefsManager) : IRatiosCache {
    private val type: Type = object : TypeToken<RatiosTimeDTO>() {}.type
    private val gson = GsonBuilder().create()

    private val source: BehaviorSubject<RatiosTimeDTO> =
        BehaviorSubject.createDefault(
            try {
                gson.fromJson(
                    sharedPrefsManager.getString(ISharedPrefsManager.Key.CachedRatios),
                    type
                )
            } catch (e: Exception) {
                // todo LOG NON FATAL TO FIREBASE
                FallbackRatios.getFallback()
            }
        )

    override fun observeCodeRatios(): Observable<RatiosTimeDTO> {
        return source
    }

    // TODO ONCE THIS WILL BE WORKING REWORK IT WOULD USE ROOM DB
    override fun saveTrackedCodes(ratios: List<CurrencyRatioDTO>): Completable =
        Completable.fromAction {
            val ratiosTime = RatiosTimeDTO(ratios, Date())

            sharedPrefsManager.put(
                ISharedPrefsManager.Key.CachedRatios,
                gson.toJson(ratiosTime, type)
            )
            source.onNext(ratiosTime)
        }
}

interface IRatiosCache {
    fun observeCodeRatios(): Observable<RatiosTimeDTO>
    fun saveTrackedCodes(ratios: List<CurrencyRatioDTO>): Completable
}
