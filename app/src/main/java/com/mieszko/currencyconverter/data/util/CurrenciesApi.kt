package com.mieszko.currencyconverter.data.util

import com.mieszko.currencyconverter.data.api.CurrenciesResponse
import io.reactivex.Single
import retrofit2.http.GET

interface CurrenciesApi {
    companion object {
        const val BASE_URL = "https://api.exchangeratesapi.io/"
    }

    @GET("latest?base=EUR")
    fun getCurrencies(): Single<CurrenciesResponse>
}