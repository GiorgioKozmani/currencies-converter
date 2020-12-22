package com.mieszko.currencyconverter.data.util

import com.google.gson.*
import com.mieszko.currencyconverter.data.api.CurrenciesResponse
import com.mieszko.currencyconverter.data.api.SingleCurrencyNetwork
import java.lang.reflect.Type


class CurrenciesResponseSerializer : JsonDeserializer<CurrenciesResponse>,
    JsonSerializer<CurrenciesResponse> {

    @Throws(JsonParseException::class)
    override fun deserialize(
        element: JsonElement,
        type: Type?,
        context: JsonDeserializationContext
    ): CurrenciesResponse {
        val rates = element.asJsonObject
            .get(RATES_KEY).asJsonObject
            .entrySet()
            .map { singleCur ->
                SingleCurrencyNetwork(singleCur.key, singleCur.value.asDouble)
            }.toMutableList()

        return CurrenciesResponse(rates)
    }

    override fun serialize(
        src: CurrenciesResponse,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonObject().apply {
            val ratesObj = JsonObject()
                .apply {
                    src.rates
                        .forEach { addProperty(it.shortName, it.ratioToBase) }
                }

            add(RATES_KEY, ratesObj)
        }
    }

    companion object {
        private const val RATES_KEY = "rates"
    }
}