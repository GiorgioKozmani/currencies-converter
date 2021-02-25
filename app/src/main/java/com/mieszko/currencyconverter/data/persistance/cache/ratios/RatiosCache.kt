package com.mieszko.currencyconverter.data.persistance.cache.ratios

import android.util.Log
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mieszko.currencyconverter.data.model.CurrencyRatioDTO
import com.mieszko.currencyconverter.data.persistance.ISharedPrefsManager
import com.mieszko.currencyconverter.domain.model.RatiosTime
import io.reactivex.rxjava3.core.Single
import java.lang.reflect.Type
import java.util.*
import kotlin.NoSuchElementException

class RatiosCache(private val sharedPrefsManager: ISharedPrefsManager) : IRatiosCache {
    private val type: Type = object : TypeToken<RatiosTimeDTO>() {}.type
    private val gson = GsonBuilder().create()

    override fun getCachedRatios(): Single<RatiosTimeDTO> {
        val savedJson = sharedPrefsManager.getString(ISharedPrefsManager.Key.CachedCurrencies)
        val cachedData: RatiosTimeDTO? = gson.fromJson(savedJson, type)
        return if (cachedData == null || cachedData.ratios.isEmpty()) {
            // TODO CUSTOM ERROR INSTEAD, SO IT'S NICELY HANDLED IN VM
            Single.error(NoSuchElementException("No ratios to show"))
        } else {
            //todo mapper for it
            Single.just(cachedData)
        }
    }

    override fun cacheRatios(ratios: List<CurrencyRatioDTO>) {
        sharedPrefsManager.put(
            ISharedPrefsManager.Key.CachedCurrencies,
            gson.toJson(RatiosTimeDTO(ratios, Date().time), type)
        )
    }
}

interface IRatiosCache {
    fun getCachedRatios(): Single<RatiosTimeDTO>
    fun cacheRatios(ratios: List<CurrencyRatioDTO>)
}