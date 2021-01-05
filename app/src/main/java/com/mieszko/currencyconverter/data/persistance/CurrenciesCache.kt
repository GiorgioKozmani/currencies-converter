package com.mieszko.currencyconverter.data.persistance

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mieszko.currencyconverter.data.api.SingleCurrencyNetwork
import com.mieszko.currencyconverter.data.util.CurrenciesResponseSerializer
import io.reactivex.Single
import java.lang.reflect.Type
import java.util.*
import kotlin.NoSuchElementException

class CurrenciesCache : ICurrenciesCache {
    private val type: Type = object : TypeToken<List<SingleCurrencyNetwork>>() {}.type
    private val gson = GsonBuilder().registerTypeAdapter(
        SingleCurrencyNetwork::class.java, CurrenciesResponseSerializer()
    ).create()

    //TODO FIX THE IMPLEMENTATION, AS NOW IM TRYING TO SAVE CURRENCY AGAIN WHEN IT'S FETCHED FROM CACHE
    override fun getCurrencies(): Single<List<SingleCurrencyNetwork>> {
        val savedJson = SharedPrefs.getString(SharedPrefs.Key.SavedCurrencies)
        val savedCurrenciesResponse: List<SingleCurrencyNetwork>? = gson.fromJson(savedJson, type)
        return if (savedCurrenciesResponse == null || savedCurrenciesResponse.isEmpty()) {
            Single.error(NoSuchElementException("No currencies to show"))
        } else {
            Single.just(savedCurrenciesResponse)
        }
    }

    override fun saveCurrencies(currencies: List<SingleCurrencyNetwork>) {
        SharedPrefs.put(SharedPrefs.Key.SavedCurrencies, gson.toJson(currencies, type))
        SharedPrefs.put(SharedPrefs.Key.SavedCurrenciesTime, Date().time)
    }
}

interface ICurrenciesCache {
    fun getCurrencies(): Single<List<SingleCurrencyNetwork>>
    fun saveCurrencies(currencies: List<SingleCurrencyNetwork>)
}