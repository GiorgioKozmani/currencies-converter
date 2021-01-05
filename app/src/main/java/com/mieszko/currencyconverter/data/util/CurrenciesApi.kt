package com.mieszko.currencyconverter.data.util

import com.mieszko.currencyconverter.data.api.SingleCurrencyNetwork
import io.reactivex.Single
import retrofit2.http.GET

interface CurrenciesApi {
    companion object {
        const val BASE_URL = "https://bank.gov.ua/NBUStatService/v1/statdirectory/"
    }

    @GET("exchange?json")
    fun getCurrencies(): Single<List<SingleCurrencyNetwork>>
}