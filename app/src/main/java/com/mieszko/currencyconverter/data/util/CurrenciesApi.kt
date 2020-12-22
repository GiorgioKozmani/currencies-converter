package com.mieszko.currencyconverter.data.util

import com.mieszko.currencyconverter.data.api.CurrenciesResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query


//interface CurrenciesApi {
//    companion object {
//        const val BASE_URL = "https://hiring.revolut.codes/api/"
//    }
//
//    @GET("android/latest")
//    fun getCurrencies(
//        @Query("base") baseCurrency: String
//    ): Single<CurrenciesResponse>
//
//}

interface CurrenciesApi {
    companion object {
        const val BASE_URL = "https://api.exchangeratesapi.io/"
    }

    @GET("latest")
    fun getCurrencies(@Query("base") baseCurrency: String): Single<CurrenciesResponse>
}