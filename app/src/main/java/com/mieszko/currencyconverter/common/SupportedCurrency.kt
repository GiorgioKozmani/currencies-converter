package com.mieszko.currencyconverter.common

import androidx.annotation.StringRes
import com.mieszko.currencyconverter.R

enum class SupportedCurrency(@StringRes val fullName: Int, val flagUrl: String) {
    AUD(R.string.aud_full, "https://img.freeflagicons.com/thumb/round_icon/australia/australia_64.png"),
    BGN(R.string.bgn_full, "https://img.freeflagicons.com/thumb/round_icon/bulgaria/bulgaria_64.png"),
    BRL(R.string.brl_full, "https://img.freeflagicons.com/thumb/round_icon/brazil/brazil_64.png"),
    CAD(R.string.cad_full, "https://img.freeflagicons.com/thumb/round_icon/canada/canada_64.png"),
    CHF(R.string.chf_full, "https://img.freeflagicons.com/thumb/round_icon/switzerland/switzerland_64.png"),
    CNY(R.string.cny_full, "https://img.freeflagicons.com/thumb/round_icon/china/china_64.png"),
    CZK(R.string.czk_full, "https://img.freeflagicons.com/thumb/round_icon/czech_republic/czech_republic_64.png"),
    DKK(R.string.dkk_full, "https://img.freeflagicons.com/thumb/round_icon/denmark/denmark_64.png"),
    EUR(R.string.eur_full, "https://img.freeflagicons.com/thumb/round_icon/european_union/european_union_64.png"),
    GBP(R.string.gbp_full, "https://img.freeflagicons.com/thumb/round_icon/united_kingdom/united_kingdom_64.png"),
    HKD(R.string.hkd_full, "https://img.freeflagicons.com/thumb/round_icon/hong_kong/hong_kong_64.png"),
    HRK(R.string.hrk_full, "https://img.freeflagicons.com/thumb/round_icon/croatia/croatia_64.png"),
    HUF(R.string.huf_full, "https://img.freeflagicons.com/thumb/round_icon/hungary/hungary_64.png"),
    ILS(R.string.ils_full, "https://img.freeflagicons.com/thumb/round_icon/israel/israel_64.png"),
    INR(R.string.inr_full, "https://img.freeflagicons.com/thumb/round_icon/india/india_64.png"),
    JPY(R.string.jpy_full, "https://img.freeflagicons.com/thumb/round_icon/japan/japan_64.png"),
    KRW(R.string.krw_full, "https://img.freeflagicons.com/thumb/round_icon/korea_south/korea_south_64.png"),
    MXN(R.string.mxn_full, "https://img.freeflagicons.com/thumb/round_icon/mexico/mexico_64.png"),
    MYR(R.string.myr_full, "https://img.freeflagicons.com/thumb/round_icon/malaysia/malaysia_64.png"),
    NOK(R.string.nok_full, "https://img.freeflagicons.com/thumb/round_icon/norway/norway_64.png"),
    NZD(R.string.nzd_full, "https://img.freeflagicons.com/thumb/round_icon/new_zealand/new_zealand_64.png"),
    PLN(R.string.pln_full, "https://img.freeflagicons.com/thumb/round_icon/poland/poland_64.png"),
    RON(R.string.ron_full, "https://img.freeflagicons.com/thumb/round_icon/romania/romania_64.png"),
    RUB(R.string.rub_full, "https://img.freeflagicons.com/thumb/round_icon/russia/russia_64.png"),
    SEK(R.string.sek_full, "https://img.freeflagicons.com/thumb/round_icon/sweden/sweden_64.png"),
    SGD(R.string.sgd_full, "https://img.freeflagicons.com/thumb/round_icon/sudan/sudan_64.png"),
    THB(R.string.thb_full, "https://img.freeflagicons.com/thumb/round_icon/thailand/thailand_64.png"),
    TRY(R.string.thb_full, "https://img.freeflagicons.com/thumb/round_icon/turkey/turkey_64.png"),
    USD(R.string.usd_full, "https://img.freeflagicons.com/thumb/round_icon/united_states_of_america/united_states_of_america_64.png"),
    ZAR(R.string.zar_full, "https://img.freeflagicons.com/thumb/round_icon/south_africa/south_africa_64.png"),
    UAH(R.string.uah_full, "https://img.freeflagicons.com/thumb/round_icon/ukraine/ukraine_64.png"),

    //TODO THIS IS NOT SUPPORTED WITH UKRAINIAN :<
    ISK(R.string.isk_full, "https://img.freeflagicons.com/thumb/round_icon/iceland/iceland_64.png"),
    IDR(R.string.idr_full, "https://img.freeflagicons.com/thumb/round_icon/indonesia/indonesia_64.png"),
    PHP(R.string.php_full, "https://img.freeflagicons.com/thumb/round_icon/philippines/philippines_64.png")

}