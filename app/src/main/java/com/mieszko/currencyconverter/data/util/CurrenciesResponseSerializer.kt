package com.mieszko.currencyconverter.data.util

import com.google.gson.*
import com.mieszko.currencyconverter.data.api.SingleCurrencyNetwork
import java.lang.reflect.Type


class CurrenciesResponseSerializer : JsonDeserializer<SingleCurrencyNetwork>
//    , JsonSerializer<SingleCurrencyNetwork>
{

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

//    override fun serialize(
//        src: SingleCurrencyNetwork,
//        typeOfSrc: Type?,
//        context: JsonSerializationContext?
//    ): JsonElement {
//        return JsonObject().apply {
//            val ratesObj = JsonObject()
//                .apply {
//                    src.forEach { addProperty(it.shortName, it.ratioToUAH) }
//                }
//
//            add(RATES_KEY, ratesObj)
//        }
//    }

}