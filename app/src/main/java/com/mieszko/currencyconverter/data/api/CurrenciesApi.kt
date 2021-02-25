package com.mieszko.currencyconverter.data.api

import com.mieszko.currencyconverter.data.model.CurrencyRatioDTO
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface CurrenciesApi {
    companion object {
        //  change into service that i controll todo this is a big issue that i do not control datasource now.
        const val BASE_URL = "https://bank.gov.ua/NBUStatService/v1/statdirectory/"
    }

    @GET("exchange?json")
    fun getCurrencies(): Single<List<CurrencyRatioDTO>>
}