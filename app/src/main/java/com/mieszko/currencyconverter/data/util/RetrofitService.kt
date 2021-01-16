package com.mieszko.currencyconverter.data.util

import com.google.gson.GsonBuilder
import com.mieszko.currencyconverter.data.api.SingleCurrencyNetwork
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitService {

    fun getCurrenciesApi(): CurrenciesApi {
        return createRetrofit().create(CurrenciesApi::class.java)
    }

    private fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(CurrenciesApi.BASE_URL)
            .client(createHttpClient())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(createGsonConverter(CurrenciesResponseSerializer()))
            .build()
    }

    private fun createHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private fun createGsonConverter(typeAdapter: Any): Converter.Factory {
        val gson = GsonBuilder()
            .apply {
                registerTypeAdapter(SingleCurrencyNetwork::class.java, typeAdapter)
            }.create()
        return GsonConverterFactory.create(gson)
    }
}