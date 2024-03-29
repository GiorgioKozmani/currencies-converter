package com.mieszko.currencyconverter.common.model

// todo rethink the naming here, as it might be misleading
// as there might be no rate for some of the "supported currencies" in the network response
enum class SupportedCode {
    AUD, BGN, BRL, CAD, CHF, CNY, CZK, DKK, EUR, GBP, HKD, HRK, HUF,
    ILS, INR, JPY, KRW, MXN, MYR, NOK, NZD, PLN, RON, RUB, SEK, SGD,
    THB, TRY, USD, ZAR, UAH, IDR, KZT, MDL, SAR, EGP, BYN, AZN, DZD,
    BDT, AMD, KGS, LBP, LYD, VND, AED, TND, UZS, RSD, TJS, GEL, IRR,
    IQD, MAD, PKR, TMT,

    // these are known to be missing in api ratios response as for 25.05.2021
    TWD, GHS
}
