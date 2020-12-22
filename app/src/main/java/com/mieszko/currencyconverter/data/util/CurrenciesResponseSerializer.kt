package com.mieszko.currencyconverter.data.util

import com.google.gson.*
import com.mieszko.currencyconverter.data.api.CurrenciesResponse
import com.mieszko.currencyconverter.data.api.SingleCurrencyNetwork
import java.lang.reflect.Type


class CurrenciesResponseSerializer : JsonDeserializer<CurrenciesResponse>,
    JsonSerializer<CurrenciesResponse> {

    private val baseCurrencyKey = "base"
    private val ratesKey = "rates"

    @Throws(JsonParseException::class)
    override fun deserialize(
        element: JsonElement,
        type: Type?,
        context: JsonDeserializationContext
    ): CurrenciesResponse {
        val obj = element.asJsonObject
        val baseCurrency = obj.get(baseCurrencyKey).asString
        val rates = obj.get(ratesKey).asJsonObject
            .entrySet()
            .map { singleCur ->
                SingleCurrencyNetwork(
                    singleCur.key,
                    singleCur.value.asDouble
                )
            }.toMutableList()

        return CurrenciesResponse(
            baseCurrency,
            rates
        )
    }

    override fun serialize(
        src: CurrenciesResponse,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonObject().apply {
            addProperty(baseCurrencyKey, "EUR")

            val ratesObj = JsonObject()
                .apply {
                    src
                        .rates
                        .forEach {
                                 addProperty(it.shortName, it.ratioToBase) }
                        }

            add(ratesKey, ratesObj)
        }
    }
}