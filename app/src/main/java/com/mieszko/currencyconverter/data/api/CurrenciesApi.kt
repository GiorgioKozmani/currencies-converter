package com.mieszko.currencyconverter.data.api

import com.mieszko.currencyconverter.data.model.CurrencyRatioDTO
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface CurrenciesApi {
    companion object {
        // It'd be optimal to change it to the service that I control. It's likely that this endpoint
        // will get changed / restricted / removed.
        const val BASE_URL = "https://bank.gov.ua/NBUStatService/v1/statdirectory/"
    }

    @GET("exchange?json")
    fun getCodeRatios(): Single<List<CurrencyRatioDTO>>
}
