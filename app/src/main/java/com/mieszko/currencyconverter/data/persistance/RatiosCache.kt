package com.mieszko.currencyconverter.data.persistance

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mieszko.currencyconverter.data.fallback.FallbackRatios
import com.mieszko.currencyconverter.data.model.CurrencyRatioDTO
import com.mieszko.currencyconverter.data.model.RatiosTimeDTO
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.lang.reflect.Type
import java.util.Date

class RatiosCache(
    private val sharedPrefsManager: ISharedPrefsManager,
    private val gson: Gson
) : IRatiosCache {
    private val type: Type = object : TypeToken<RatiosTimeDTO>() {}.type

    private val source: BehaviorSubject<RatiosTimeDTO> =
        BehaviorSubject.createDefault(
            try {
                gson.fromJson(
                    sharedPrefsManager.getString(ISharedPrefsManager.Key.CachedRatios),
                    type
                )
            } catch (e: Exception) {
                // Load a fallback if there's no fresh ratios data to show.
                FallbackRatios.getFallback()
            }
        )

    override fun observeCodeRatios(): Observable<RatiosTimeDTO> {
        return source
    }

    override fun saveCodeRatios(ratios: List<CurrencyRatioDTO>): Completable =
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
    /**
     * Emits latest known ratios on
     * - every subscription
     * - ratios update
     */
    fun observeCodeRatios(): Observable<RatiosTimeDTO>

    /**
     * Caches ratios, causes [observeCodeRatios] to emit an update.
     */
    fun saveCodeRatios(ratios: List<CurrencyRatioDTO>): Completable
}
