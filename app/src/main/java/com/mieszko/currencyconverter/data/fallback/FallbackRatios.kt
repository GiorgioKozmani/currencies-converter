package com.mieszko.currencyconverter.data.fallback

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mieszko.currencyconverter.data.model.CurrencyRatioDTO
import com.mieszko.currencyconverter.data.model.RatiosTimeDTO
import java.lang.reflect.Type
import java.util.Calendar

/**
 * These are the ratios that are meant to be loaded when there's no any data available.
 */
class FallbackRatios {

    companion object {
        fun getFallback(): RatiosTimeDTO {
            val ratiosType: Type = object : TypeToken<List<CurrencyRatioDTO>>() {}.type
            return RatiosTimeDTO(
                Gson().fromJson(
                    fallbackJson,
                    ratiosType
                ),
                fallbackDate
            )
        }

        private val fallbackDate = Calendar.getInstance().apply {
            set(Calendar.YEAR, 2021)
            set(Calendar.MONTH, Calendar.JUNE)
            set(Calendar.DAY_OF_MONTH, 17)
            set(Calendar.HOUR_OF_DAY, 12)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }.time

        private const val fallbackJson =
            """[
    {
    "r030":36,"txt":"Австралійський долар","rate":20.8477,"cc":"AUD","exchangedate":"17.06.2021"
     }
    ,{
    "r030":124,"txt":"Канадський долар","rate":22.1983,"cc":"CAD","exchangedate":"17.06.2021"
     }
    ,{
    "r030":156,"txt":"Юань Женьміньбі","rate":4.2241,"cc":"CNY","exchangedate":"17.06.2021"
     }
    ,{
    "r030":191,"txt":"Куна","rate":4.3712,"cc":"HRK","exchangedate":"17.06.2021"
     }
    ,{
    "r030":203,"txt":"Чеська крона","rate":1.2854,"cc":"CZK","exchangedate":"17.06.2021"
     }
    ,{
    "r030":208,"txt":"Данська крона","rate":4.4069,"cc":"DKK","exchangedate":"17.06.2021"
     }
    ,{
    "r030":344,"txt":"Гонконгівський долар","rate":3.482,"cc":"HKD","exchangedate":"17.06.2021"
     }
    ,{
    "r030":348,"txt":"Форинт","rate":0.09307,"cc":"HUF","exchangedate":"17.06.2021"
     }
    ,{
    "r030":356,"txt":"Індійська рупія","rate":0.36862,"cc":"INR","exchangedate":"17.06.2021"
     }
    ,{
    "r030":360,"txt":"Рупія","rate":0.001898,"cc":"IDR","exchangedate":"17.06.2021"
     }
    ,{
    "r030":376,"txt":"Новий ізраїльський шекель","rate":8.3439,"cc":"ILS","exchangedate":"17.06.2021"
     }
    ,{
    "r030":392,"txt":"Єна","rate":0.24607,"cc":"JPY","exchangedate":"17.06.2021"
     }
    ,{
    "r030":398,"txt":"Теньге","rate":0.063423,"cc":"KZT","exchangedate":"17.06.2021"
     }
    ,{
    "r030":410,"txt":"Вона","rate":0.024197,"cc":"KRW","exchangedate":"17.06.2021"
     }
    ,{
    "r030":484,"txt":"Мексиканське песо","rate":1.3517,"cc":"MXN","exchangedate":"17.06.2021"
     }
    ,{
    "r030":498,"txt":"Молдовський лей","rate":1.5266,"cc":"MDL","exchangedate":"17.06.2021"
     }
    ,{
    "r030":554,"txt":"Новозеландський долар","rate":19.326,"cc":"NZD","exchangedate":"17.06.2021"
     }
    ,{
    "r030":578,"txt":"Норвезька крона","rate":3.2351,"cc":"NOK","exchangedate":"17.06.2021"
     }
    ,{
    "r030":643,"txt":"Російський рубль","rate":0.37555,"cc":"RUB","exchangedate":"17.06.2021"
     }
    ,{
    "r030":682,"txt":"Саудівський ріял","rate":7.2069,"cc":"SAR","exchangedate":"17.06.2021"
     }
    ,{
    "r030":702,"txt":"Сінгапурський долар","rate":20.3858,"cc":"SGD","exchangedate":"17.06.2021"
     }
    ,{
    "r030":710,"txt":"Ренд","rate":1.9657,"cc":"ZAR","exchangedate":"17.06.2021"
     }
    ,{
    "r030":752,"txt":"Шведська крона","rate":3.2352,"cc":"SEK","exchangedate":"17.06.2021"
     }
    ,{
    "r030":756,"txt":"Швейцарський франк","rate":30.0824,"cc":"CHF","exchangedate":"17.06.2021"
     }
    ,{
    "r030":818,"txt":"Єгипетський фунт","rate":1.7264,"cc":"EGP","exchangedate":"17.06.2021"
     }
    ,{
    "r030":826,"txt":"Фунт стерлінгів","rate":38.1696,"cc":"GBP","exchangedate":"17.06.2021"
     }
    ,{
    "r030":840,"txt":"Долар США","rate":27.0275,"cc":"USD","exchangedate":"17.06.2021"
     }
    ,{
    "r030":933,"txt":"Білоруський рубль","rate":10.8649,"cc":"BYN","exchangedate":"17.06.2021"
     }
    ,{
    "r030":946,"txt":"Румунський лей","rate":6.6567,"cc":"RON","exchangedate":"17.06.2021"
     }
    ,{
    "r030":949,"txt":"Турецька ліра","rate":3.1629,"cc":"TRY","exchangedate":"17.06.2021"
     }
    ,{
    "r030":960,"txt":"СПЗ (спеціальні права запозичення)","rate":38.9238,"cc":"XDR","exchangedate":"17.06.2021"
     }
    ,{
    "r030":975,"txt":"Болгарський лев","rate":16.754,"cc":"BGN","exchangedate":"17.06.2021"
     }
    ,{
    "r030":978,"txt":"Євро","rate":32.7722,"cc":"EUR","exchangedate":"17.06.2021"
     }
    ,{
    "r030":985,"txt":"Злотий","rate":7.2526,"cc":"PLN","exchangedate":"17.06.2021"
     }
    ,{
    "r030":12,"txt":"Алжирський динар","rate":0.20596,"cc":"DZD","exchangedate":"17.06.2021"
     }
    ,{
    "r030":50,"txt":"Така","rate":0.32391,"cc":"BDT","exchangedate":"17.06.2021"
     }
    ,{
    "r030":51,"txt":"Вірменський драм","rate":0.05276,"cc":"AMD","exchangedate":"17.06.2021"
     }
    ,{
    "r030":364,"txt":"Іранський ріал","rate":0.00065399,"cc":"IRR","exchangedate":"17.06.2021"
     }
    ,{
    "r030":368,"txt":"Іракський динар","rate":0.018813,"cc":"IQD","exchangedate":"17.06.2021"
     }
    ,{
    "r030":417,"txt":"Сом","rate":0.32849,"cc":"KGS","exchangedate":"17.06.2021"
     }
    ,{
    "r030":422,"txt":"Ліванський фунт","rate":0.01822,"cc":"LBP","exchangedate":"17.06.2021"
     }
    ,{
    "r030":434,"txt":"Лівійський динар","rate":6.1684,"cc":"LYD","exchangedate":"17.06.2021"
     }
    ,{
    "r030":458,"txt":"Малайзійський ринггіт","rate":6.6597,"cc":"MYR","exchangedate":"17.06.2021"
     }
    ,{
    "r030":504,"txt":"Марокканський дирхам","rate":3.1071,"cc":"MAD","exchangedate":"17.06.2021"
     }
    ,{
    "r030":586,"txt":"Пакистанська рупія","rate":0.1782,"cc":"PKR","exchangedate":"17.06.2021"
     }
    ,{
    "r030":704,"txt":"Донг","rate":0.0011917,"cc":"VND","exchangedate":"17.06.2021"
     }
    ,{
    "r030":764,"txt":"Бат","rate":0.88025,"cc":"THB","exchangedate":"17.06.2021"
     }
    ,{
    "r030":784,"txt":"Дирхам ОАЕ","rate":7.4782,"cc":"AED","exchangedate":"17.06.2021"
     }
    ,{
    "r030":788,"txt":"Туніський динар","rate":10.0492,"cc":"TND","exchangedate":"17.06.2021"
     }
    ,{
    "r030":860,"txt":"Узбецький сум","rate":0.0025991,"cc":"UZS","exchangedate":"17.06.2021"
     }
    ,{
    "r030":934,"txt":"Туркменський новий манат","rate":7.8478,"cc":"TMT","exchangedate":"17.06.2021"
     }
    ,{
    "r030":941,"txt":"Сербський динар","rate":0.28493,"cc":"RSD","exchangedate":"17.06.2021"
     }
    ,{
    "r030":944,"txt":"Азербайджанський манат","rate":16.1649,"cc":"AZN","exchangedate":"17.06.2021"
     }
    ,{
    "r030":972,"txt":"Сомоні","rate":2.4264,"cc":"TJS","exchangedate":"17.06.2021"
     }
    ,{
    "r030":981,"txt":"Ларі","rate":8.4326,"cc":"GEL","exchangedate":"17.06.2021"
     }
    ,{
    "r030":986,"txt":"Бразильський реал","rate":5.2528,"cc":"BRL","exchangedate":"17.06.2021"
     }
    ,{
    "r030":959,"txt":"Золото","rate":50215.47,"cc":"XAU","exchangedate":"17.06.2021"
     }
    ,{
    "r030":961,"txt":"Срібло","rate":750.54,"cc":"XAG","exchangedate":"17.06.2021"
     }
    ,{
    "r030":962,"txt":"Платина","rate":31080.27,"cc":"XPT","exchangedate":"17.06.2021"
     }
    ,{
    "r030":964,"txt":"Паладій","rate":74775.63,"cc":"XPD","exchangedate":"17.06.2021"
     }
]"""
    }
}
