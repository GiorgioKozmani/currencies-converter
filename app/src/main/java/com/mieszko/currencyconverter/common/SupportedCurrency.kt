package com.mieszko.currencyconverter.common

import androidx.annotation.StringRes
import com.mieszko.currencyconverter.R

//TODO IMPORTANT NOTE! WHEN THE APP ENABLES MONETIZATION, I MIGHT NEED TO BUY LICENCE FOR THESE! IT'S 1$/EACH, AND IT'D LOAD IMMEDIATELY SO IT'S WORTH IT IMO
// or just search for free ones, like https://www.flaticon.com/packs/countrys-flags/
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
    IDR(R.string.idr_full, "https://img.freeflagicons.com/thumb/round_icon/indonesia/indonesia_64.png"),
    KZT(R.string.kzt_full, "https://img.freeflagicons.com/thumb/round_icon/kazakhstan/kazakhstan_64.png"),
    MDL(R.string.mdl_full, "https://img.freeflagicons.com/thumb/round_icon/moldova/moldova_64.png"),
    SAR(R.string.sar_full, "https://img.freeflagicons.com/thumb/round_icon/saudi_arabia/saudi_arabia_64.png"),
    EGP(R.string.egp_full, "https://img.freeflagicons.com/thumb/round_icon/egypt/egypt_64.png"),
    BYN(R.string.byn_full, "https://img.freeflagicons.com/thumb/round_icon/belarus/belarus_64.png"),
    AZN(R.string.azn_full, "https://img.freeflagicons.com/thumb/round_icon/azerbaijan/azerbaijan_64.png"),
    DZD(R.string.dzd_full, "https://img.freeflagicons.com/thumb/round_icon/algeria/algeria_64.png"),
    BDT(R.string.bdt_full, "https://img.freeflagicons.com/thumb/round_icon/bangladesh/bangladesh_64.png"),
    AMD(R.string.amd_full, "https://img.freeflagicons.com/thumb/round_icon/armenia/armenia_64.png"),
    KGS(R.string.kgs_full, "https://img.freeflagicons.com/thumb/round_icon/kyrgyzstan/kyrgyzstan_64.png"),
    LBP(R.string.lbp_full, "https://img.freeflagicons.com/thumb/round_icon/lebanon/lebanon_64.png"),
    LYD(R.string.lyd_full, "https://img.freeflagicons.com/thumb/round_icon/libya/libya_64.png"),
    VND(R.string.vnd_full, "https://img.freeflagicons.com/thumb/round_icon/vietnam/vietnam_64.png"),
    AED(R.string.aed_full, "https://img.freeflagicons.com/thumb/round_icon/united_arab_emirates/united_arab_emirates_64.png"),
    TND(R.string.tnd_full, "https://img.freeflagicons.com/thumb/round_icon/tunisia/tunisia_64.png"),
    UZS(R.string.uzs_full, "https://img.freeflagicons.com/thumb/round_icon/uzbekistan/uzbekistan_64.png"),
    TWD(R.string.twd_full, "https://img.freeflagicons.com/thumb/round_icon/republic_of_china/republic_of_china_64.png"),
    GHS(R.string.ghs_full, "https://img.freeflagicons.com/thumb/round_icon/ghana/ghana_64.png"),
    RSD(R.string.rsd_full, "https://img.freeflagicons.com/thumb/round_icon/serbia/serbia_64.png"),
    TJS(R.string.tjs_full, "https://img.freeflagicons.com/thumb/round_icon/tajikistan/tajikistan_64.png"),
    GEL(R.string.gel_full, "https://img.freeflagicons.com/thumb/round_icon/georgia/georgia_64.png")
//    //TODO THIS IS NOT SUPPORTED WITH UKRAINIAN :< consider what to do with these as they ARE available in the european bank
//    ISK(R.string.isk_full, "https://img.freeflagicons.com/thumb/round_icon/iceland/iceland_64.png"),
//    PHP(R.string.php_full, "https://img.freeflagicons.com/thumb/round_icon/philippines/philippines_64.png")

    // TODO CONSIDER WHAT TO DO WITH THESE
//    XAU(R.string.gold_full, "https://img.freeflagicons.com/thumb/round_icon/georgia/georgia_64.png"),
//    XAG(R.string.silver_full, "https://img.freeflagicons.com/thumb/round_icon/georgia/georgia_64.png"),
}