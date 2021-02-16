package com.mieszko.currencyconverter.data.persistance

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mieszko.currencyconverter.data.api.SingleCurrencyNetwork
import io.reactivex.rxjava3.core.Single
import java.lang.reflect.Type
import java.util.*
import kotlin.NoSuchElementException

class CurrenciesCache(private val sharedPrefsManager: ISharedPrefsManager) : ICurrenciesCache {
    private val type: Type = object : TypeToken<List<SingleCurrencyNetwork>>() {}.type
    private val gson = GsonBuilder().create()

    override fun getCurrencies(): Single<List<SingleCurrencyNetwork>> {
        val savedJson = sharedPrefsManager.getString(ISharedPrefsManager.Key.CachedCurrencies)
        val savedCurrenciesResponse: List<SingleCurrencyNetwork>? = gson.fromJson(savedJson, type)
        return if (savedCurrenciesResponse == null || savedCurrenciesResponse.isEmpty()) {
            Single.error(NoSuchElementException("No currencies to show"))
        } else {
            Single.just(savedCurrenciesResponse)
        }
    }

    override fun saveCurrencies(currencies: List<SingleCurrencyNetwork>) {
        sharedPrefsManager.put(ISharedPrefsManager.Key.CachedCurrencies, gson.toJson(currencies, type))
        sharedPrefsManager.put(ISharedPrefsManager.Key.CachedCurrenciesTime, Date().time)
    }
}

interface ICurrenciesCache {
    fun getCurrencies(): Single<List<SingleCurrencyNetwork>>
    fun saveCurrencies(currencies: List<SingleCurrencyNetwork>)
}