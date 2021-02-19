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

    override fun getTrackedCurrencies(): Observable<List<SupportedCurrency>> {
        return source
    }

    // TODO HANDLE ERRORS INSTEAD OF DOING NOTHING

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

    override fun moveTrackedCurrencyToTop(trackedCurrency: SupportedCurrency): Completable =
        Completable.fromCallable {
            source.value.let { currentList ->
                if (currentList.contains(trackedCurrency)) {
                    val newList = currentList.toMutableList()
                        .apply {
                            remove(trackedCurrency)
                            add(0, trackedCurrency)
                        }
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

    override fun swapTrackingOrder(
        firstCurrency: SupportedCurrency,
        secondCurrency: SupportedCurrency
    ): Completable =
        Completable.fromCallable {
            source.value.let { currentList ->
                val newList = currentList.toMutableList()
                Collections.swap(
                    newList,
                    newList.indexOf(firstCurrency),
                    newList.indexOf(secondCurrency)
                )
                sharedPrefsManager.put(
                    ISharedPrefsManager.Key.TrackedCurrencies,
                    gson.toJson(newList, supportedCurrencyType)
                )
                source.onNext(newList)
            }
        }
}

interface ITrackedCurrenciesRepository {
    //todo javadoc
    fun getTrackedCurrencies(): Observable<List<SupportedCurrency>>

    fun addTrackedCurrency(trackedCurrency: SupportedCurrency): Completable

    fun removeTrackedCurrency(untrackedCurrency: SupportedCurrency): Completable

    fun swapTrackingOrder(
        firstCurrency: SupportedCurrency,
        secondCurrency: SupportedCurrency
    ): Completable

    fun moveTrackedCurrencyToTop(trackedCurrency: SupportedCurrency): Completable
}