package com.mieszko.currencyconverter.data.repository

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mieszko.currencyconverter.common.SupportedCurrency
import com.mieszko.currencyconverter.data.persistance.ISharedPrefsManager
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.lang.reflect.Type
import java.util.*

class TrackedCurrenciesRepository(
    private val sharedPrefsManager: ISharedPrefsManager
) : ITrackedCurrenciesRepository {

    private val supportedCurrencyType: Type = object : TypeToken<List<SupportedCurrency>>() {}.type
    private val gson: Gson = GsonBuilder().create()

    private val source: BehaviorSubject<List<SupportedCurrency>> =
        BehaviorSubject.createDefault(
            gson.fromJson(
                sharedPrefsManager.getString(ISharedPrefsManager.Key.TrackedCurrencies, "[]"),
                supportedCurrencyType
            )
        )

    //TODO ONE SOURCE OF TRUTH, THIS IS CRUCIAL. SO BOTH SELECTION FRAGMENT AND LIST FRAGMENT ONLY EMIT DATA FROM THERE! it might be slower but is better, check performance
    override fun getTrackedCurrencies(): Observable<List<SupportedCurrency>> {
        return source
    }

    //todo emit errors if it's already present
    override fun addTrackedCurrency(trackedCurrency: SupportedCurrency): Completable =
        Completable.fromCallable {
            source.value.let { currentList ->
                if (!currentList.contains(trackedCurrency)) {
                    val newList = currentList.toMutableList().apply { add(0, trackedCurrency) }
                    sharedPrefsManager.put(
                        ISharedPrefsManager.Key.TrackedCurrencies,
                        gson.toJson(newList, supportedCurrencyType)
                    )
                    source.onNext(newList)
                } else {
                    source.onNext(currentList)
                }
            }
        }

    override fun removeTrackedCurrency(untrackedCurrency: SupportedCurrency): Completable =
        Completable.fromCallable {
            source.value.let { currentList ->
                if (currentList.contains(untrackedCurrency)) {
                    //todo change actual data, lol
                    val newList = currentList.toMutableList().apply { remove(untrackedCurrency) }
                    sharedPrefsManager.put(
                        ISharedPrefsManager.Key.TrackedCurrencies,
                        gson.toJson(newList, supportedCurrencyType)
                    )
                    source.onNext(newList)
                } else {
                    source.onNext(currentList)
                }
            }
        }

    //todo consider currencies instead
    override fun changeTrackingOrder(from: Int, to: Int): Completable =
        Completable.fromCallable {
            source.value.let { currentList ->
                //todo change actual data, lol
                val newList = currentList.toMutableList()
                Collections.swap(newList, from, to)
                sharedPrefsManager.put(
                    ISharedPrefsManager.Key.TrackedCurrencies,
                    gson.toJson(newList, supportedCurrencyType)
                )
                source.onNext(newList)
            }
        }


}

interface ITrackedCurrenciesRepository {
    fun getTrackedCurrencies(): Observable<List<SupportedCurrency>>
    fun addTrackedCurrency(trackedCurrency: SupportedCurrency): Completable
    fun removeTrackedCurrency(untrackedCurrency: SupportedCurrency): Completable
    fun changeTrackingOrder(from: Int, to: Int): Completable
}