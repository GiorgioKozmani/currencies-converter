package com.mieszko.currencyconverter.data.repository

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mieszko.currencyconverter.common.SupportedCode
import com.mieszko.currencyconverter.data.persistance.ISharedPrefsManager
import com.mieszko.currencyconverter.domain.repository.ITrackedCurrenciesRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.lang.reflect.Type
import java.util.*

//todo create usecases, only set / get methods in repository. Viewmodel should talk to usecases instead
class TrackedCodesRepository(
    private val sharedPrefsManager: ISharedPrefsManager
) : ITrackedCurrenciesRepository {

    private val supportedCurrencyType: Type = object : TypeToken<List<SupportedCode>>() {}.type
    private val gson: Gson = GsonBuilder().create()

    private val source: BehaviorSubject<List<SupportedCode>> =
        BehaviorSubject.createDefault(
            gson.fromJson(
                sharedPrefsManager.getString(ISharedPrefsManager.Key.TrackedCurrencies, "[]"),
                supportedCurrencyType
            )
        )

    override fun getTrackedCurrencies(): Observable<List<SupportedCode>> {
        return source
    }

    // TODO HANDLE ERRORS INSTEAD OF DOING NOTHING

    override fun addTrackedCurrency(trackedCurrency: SupportedCode): Completable =
        Completable.fromCallable {
            source.value.let { currentList ->
                if (!currentList.contains(trackedCurrency)) {
                    val newList = currentList.toMutableList().apply { add(trackedCurrency) }
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

    override fun removeTrackedCurrency(untrackedCurrency: SupportedCode): Completable =
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

    override fun moveTrackedCurrencyToTop(trackedCurrency: SupportedCode): Completable =
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
        firstCurrency: SupportedCode,
        secondCurrency: SupportedCode
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