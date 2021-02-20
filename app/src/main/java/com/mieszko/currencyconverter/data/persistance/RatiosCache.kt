package com.mieszko.currencyconverter.data.persistance

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mieszko.currencyconverter.data.api.model.CurrencyRatioDTO
import io.reactivex.rxjava3.core.Single
import java.lang.reflect.Type
import java.util.*
import kotlin.NoSuchElementException

class RatiosCache(private val sharedPrefsManager: ISharedPrefsManager) : IRatiosCache {
    private val type: Type = object : TypeToken<List<CurrencyRatioDTO>>() {}.type
    private val gson = GsonBuilder().create()

    override fun getCachedRatios(): Single<List<CurrencyRatioDTO>> {
        val savedJson = sharedPrefsManager.getString(ISharedPrefsManager.Key.CachedCurrencies)
        val savedCurrenciesResponse: List<CurrencyRatioDTO>? = gson.fromJson(savedJson, type)
        return if (savedCurrenciesResponse == null || savedCurrenciesResponse.isEmpty()) {
            // TODO CUSTOM ERROR INSTEAD, SO IT'S NICELY HANDLED IN VM
            Single.error(NoSuchElementException("No ratios to show"))
        } else {
            Single.just(savedCurrenciesResponse)
        }
    }

    override fun cacheRatios(ratios: List<CurrencyRatioDTO>) {
        sharedPrefsManager.run {
            put(ISharedPrefsManager.Key.CachedCurrencies, gson.toJson(ratios, type))
            put(ISharedPrefsManager.Key.CachedCurrenciesTime, Date().time)
        }
    }
}

interface IRatiosCache {
    fun getCachedRatios(): Single<List<CurrencyRatioDTO>>
    fun cacheRatios(ratios: List<CurrencyRatioDTO>)
}