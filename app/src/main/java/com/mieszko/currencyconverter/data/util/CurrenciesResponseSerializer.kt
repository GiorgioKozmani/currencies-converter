package com.mieszko.currencyconverter.data.util

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.mieszko.currencyconverter.data.api.SingleCurrencyNetwork
import java.lang.reflect.Type


class CurrenciesResponseSerializer : JsonDeserializer<SingleCurrencyNetwork> {

    @Throws(JsonParseException::class)
    override fun deserialize(
        element: JsonElement,
        type: Type?,
        context: JsonDeserializationContext
    ): SingleCurrencyNetwork {
        val obj = element.asJsonObject
        val shortName = obj.get("cc").asString
        val rate = element.asJsonObject.get("rate").asDouble

        return SingleCurrencyNetwork(shortName = shortName, ratioToUAH = rate)
    }
}