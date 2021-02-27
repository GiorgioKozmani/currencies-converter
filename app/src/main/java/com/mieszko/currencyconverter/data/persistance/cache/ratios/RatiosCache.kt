package com.mieszko.currencyconverter.data.persistance.cache.ratios

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mieszko.currencyconverter.common.Resource
import com.mieszko.currencyconverter.data.model.CurrencyRatioDTO
import com.mieszko.currencyconverter.data.persistance.ISharedPrefsManager
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.lang.reflect.Type
import java.util.*

class RatiosCache(private val sharedPrefsManager: ISharedPrefsManager) : IRatiosCache {
    private val type: Type = object : TypeToken<RatiosTimeDTO>() {}.type
    private val gson = GsonBuilder().create()

    //todo check
//    One of the issues with Subject is that after it receives onComplete() or onError() – it's no longer able to move data. Sometimes it's the desired behavior, but sometimes it's not.
//    In cases when such behavior isn't desired, we should consider using RxRelay.
//    https://www.baeldung.com/rx-relay
    //TODO INSTEAD OF ERROR GO FOR RXRELAY!!
    private val source: BehaviorSubject<Resource<RatiosTimeDTO>> =
        BehaviorSubject.createDefault(
            when (val persistedRatios = gson.fromJson<RatiosTimeDTO>(
                sharedPrefsManager.getString(ISharedPrefsManager.Key.CachedRatios),
                type
            )) {
                null -> {
                    Resource.Error("NO RATIOS ARE AVAILABLE")
                }
                else -> {
                    Resource.Success(persistedRatios)
                }
            }
        )

    override fun observeCodeRatios(): Observable<Resource<RatiosTimeDTO>> {
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
            source.onNext(Resource.Success(ratiosTime))
        }

}

interface IRatiosCache {
    fun observeCodeRatios(): Observable<Resource<RatiosTimeDTO>>
    fun saveTrackedCodes(ratios: List<CurrencyRatioDTO>): Completable
}