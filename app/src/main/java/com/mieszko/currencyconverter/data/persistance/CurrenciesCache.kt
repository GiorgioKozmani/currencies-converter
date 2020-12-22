package com.mieszko.currencyconverter.data.persistance

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mieszko.currencyconverter.data.api.CurrenciesResponse
import com.mieszko.currencyconverter.data.util.CurrenciesResponseSerializer
import io.reactivex.Single
import java.lang.reflect.Type
import java.util.*
import kotlin.NoSuchElementException

class CurrenciesCache : ICurrenciesCache {
    private val type: Type = object : TypeToken<CurrenciesResponse>() {}.type
    private val gson = GsonBuilder().registerTypeAdapter(
        CurrenciesResponse::class.java, CurrenciesResponseSerializer()
    ).create()

    override fun getCurrencies(): Single<CurrenciesResponse> {
        val savedJson = SharedPrefs.getString(SharedPrefs.Key.SavedCurrencies)
        val savedCurrenciesResponse: CurrenciesResponse? = gson.fromJson(savedJson, type)
        return if (savedCurrenciesResponse == null || savedCurrenciesResponse.rates.isEmpty()) {
            Single.error(NoSuchElementException("No currencies to show"))
        } else {
            Single.just(savedCurrenciesResponse)
        }
    }

    override fun saveCurrencies(dynamicFields: CurrenciesResponse) {
        SharedPrefs.put(SharedPrefs.Key.SavedCurrencies, gson.toJson(dynamicFields, type))
        SharedPrefs.put(SharedPrefs.Key.SavedCurrenciesTime, Date().time)
    }
}

interface ICurrenciesCache {
    fun getCurrencies(): Single<CurrenciesResponse>
    fun saveCurrencies(dynamicFields: CurrenciesResponse)
}