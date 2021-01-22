package com.mieszko.currencyconverter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mieszko.currencyconverter.common.SupportedCurrency
import com.mieszko.currencyconverter.data.model.CurrencyDataModel
import com.mieszko.currencyconverter.data.model.CurrencyListItemModel
import com.mieszko.currencyconverter.data.model.Resource
import com.mieszko.currencyconverter.data.persistance.SharedPrefs
import com.mieszko.currencyconverter.data.repository.ICurrenciesRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.net.UnknownHostException
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToLong


class CurrenciesViewModel(private val dataRepository: ICurrenciesRepository) : ViewModel() {
    private val disposeBag = CompositeDisposable()

    private val currenciesListModelsLiveData: MutableLiveData<Resource<List<CurrencyListItemModel>>> =
        MutableLiveData()
    private val lastUpdatedLiveData: MutableLiveData<Resource<Date>> =
        MutableLiveData()

    private var currenciesDataList = mutableListOf<CurrencyDataModel>()

    //Exposing only LiveData
    fun getCurrenciesLiveDate(): LiveData<Resource<List<CurrencyListItemModel>>> =
        currenciesListModelsLiveData

    fun getLastUpdatedLiveData(): LiveData<Resource<Date>> =
        lastUpdatedLiveData

    private lateinit var baseCurrencyDataModel: CurrencyDataModel

    init {
        setupSupportedCurrencies()
        setBaseCurrency(SupportedCurrency.UAH)
        startUpdatingRates()
    }

    fun setBaseCurrencyAmount(newAmount: Double) {
//        val newValue = newAmountText.sanitizeBaseValue().toValidDouble()
        //todo rollback if same check
        Completable.fromAction {
            if (newAmount != baseCurrencyDataModel.amount) {
                baseCurrencyDataModel.amount = newAmount
                //TODO INTO BG THREAD
                currenciesDataList.forEach { supportedCurrencyData ->
                    if (supportedCurrencyData != baseCurrencyDataModel) {
                        supportedCurrencyData.amount =
                            calculateCurrencyAmount(supportedCurrencyData)
                    }
                }
                emitLiveData()
            }
        }
            .subscribeOn(Schedulers.computation())
            .subscribe()
    }

    // Move new base currency to the top of the list, and inform adapter about the change so it's instantly animated
    fun setBaseCurrency(newBaseCurrency: SupportedCurrency) {
        Completable.fromAction {
            baseCurrencyDataModel = currenciesDataList.find { it.currency == newBaseCurrency }!!
            moveBaseCurrencyTop()
            emitLiveData()
        }.subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun moveItem(from: Int, to: Int) {

        //todo on bg thread?
        Completable.fromAction {
            Collections.swap(currenciesDataList, from, to)
//
//            val fromItem = currenciesDataList[from]
//            currenciesDataList.removeAt(from)
//            if (to < from) {
//                currenciesDataList.add(to, fromItem)
//            } else {
//                currenciesDataList.add(to - 1, fromItem)
//            }
            // after move reset base and emit
            baseCurrencyDataModel = currenciesDataList[0]
            emitLiveData()
        }.subscribeOn(Schedulers.io())
            .subscribe()
    }

    private fun calculateCurrencyAmount(supportedCurrency: CurrencyDataModel) =
        try {
            (baseCurrencyDataModel.toUAHRatio / supportedCurrency.toUAHRatio * baseCurrencyDataModel.amount).roundTo2Decimals()
        } catch (t: Throwable) {
            //todo TO CRASHLYTICS
            0.0
        }

    private fun moveBaseCurrencyTop() {
        //todo to bg thread?
        currenciesDataList.remove(baseCurrencyDataModel)
        currenciesDataList.add(0, baseCurrencyDataModel)
    }

    private fun startUpdatingRates() {
        disposeBag.add(
            //todo as this is updated once in a day at 16, this would be nice to have it added as a (?) button, together with refresh button but don't update it every second
//            Observable.interval(5, TimeUnit.SECONDS)
//                // Thanks to liveData.postValue, we can move whole flow to the background thread.
//                .observeOn(Schedulers.io())
//                .retry()
//                .subscribe(
//                    {
            dataRepository
                .loadCurrencies()
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { currenciesResponse ->
                        //todo simplify it, it might be better to do this other way around, and eliminate nulls
                        currenciesResponse
                            .forEach { singleCurrencyResponse ->
                                currenciesDataList
                                    .find { it.currency.name == singleCurrencyResponse.shortName }
                                    ?.apply {
                                        if (this != baseCurrencyDataModel) {
                                            toUAHRatio =
                                                singleCurrencyResponse.ratioToUAH
                                            amount = calculateCurrencyAmount(this)
                                        }
                                    }
                            }

                        emitLiveData()
                    },
                    { t ->
                        emitError(t)
                    }
                )
//                    },
//                    { t ->
//                        emitError(t)
//                    }
//                )
        )
    }

    private fun emitError(t: Throwable?) {
        val errorMessage = when (t) {
            is UnknownHostException -> {
                "Not updated, turn on internet."
            }
            is NoSuchElementException -> {
                "Nothing to show, turn on internet."
            }
            else -> {
                "Error occurred"
            }
        }
        currenciesListModelsLiveData.postValue(Resource.Error(errorMessage))
    }

    // Handled in the background thread, in order to avoid blocking UI thread by the mapping,
    // hence postValue, not setValue is used
    private fun emitLiveData() {
        // Posts a task to a main thread to set the given value.
        // If this method is called multiple times before a main thread executed a posted task,
        // only the last value would be dispatched.
        emitCurrencies()
        emitUpdateDate()
    }

    private fun emitUpdateDate() {
        val dateLong = SharedPrefs.getLong(SharedPrefs.Key.SavedCurrenciesTime)
        val dateResource =
            if (dateLong != (-1).toLong()) {
                Resource.Success(Date(dateLong))
            } else {
                Resource.Error("Never updated")
            }
        lastUpdatedLiveData.postValue(dateResource)
    }

    private fun emitCurrencies() {
        getListItemModels()
            .subscribeOn(Schedulers.io())
            .subscribe { values ->
                currenciesListModelsLiveData.postValue(Resource.Success(values))
            }
    }

    private fun getListItemModels(): Single<List<CurrencyListItemModel>> {
        //todo this is waste of resources
        //todo hashmap with key as currency?
        return Single.just(currenciesDataList
            .map { currencyModel ->
                CurrencyListItemModel(
                    currency = currencyModel.currency,
                    //todo verify
                    //todo trim last zeroes?
//                    amountText = currencyModel.amount.toBigDecimal().toPlainString(),
                    amount = currencyModel.amount.roundTo2Decimals(),
                    //TODO REVERT CACHE
                    //TODO THIS WILL RETURN SOME CRAP BEFORE FETCHING ITEMS, BECAUSE UAH HAS RATIO SET
                    baseToThisText = makeRatioString(currencyModel, baseCurrencyDataModel),
                    thisToBaseText = makeRatioString(baseCurrencyDataModel, currencyModel)
                )
            })


    }

    private fun makeRatioString(
        firstCurrency: CurrencyDataModel,
        secondCurrency: CurrencyDataModel
    ) = if (secondCurrency.toUAHRatio != 0.0) {
        "1 ${firstCurrency.currency.name} ≈ ${(firstCurrency.toUAHRatio / secondCurrency.toUAHRatio).roundTo2Decimals()} ${secondCurrency.currency.name}"
    } else {
        //todo TO CRASHLYTICS
        ""
    }

    private fun setupSupportedCurrencies() {
        //todo change ordering here?
        SupportedCurrency
            .values()
            .mapTo(currenciesDataList) { currency ->
                CurrencyDataModel(currency)
                    .apply {
                        if (currency == SupportedCurrency.UAH) {
                            // initial setup for base currency
                            toUAHRatio = 1.0; amount = 100.0
                        }
                    }
            }
    }

    private fun Double.roundTo2Decimals(): Double {
        val factor = 10.0.pow(2)
        return (this * factor).roundToLong() / factor
//        return (this * factor) / factor
    }

//    private fun String.sanitizeBaseValue(): String {
//        //todo ideo -> disable text watcher during "set / update" text executes???
//
//        //todo handle "" ?
////        if (this.trim() == ""){
////            return "0"
////        }
//
//        if (this.trim() == ".") {
//            return "0."
//        }
//
//        var strToReturn = this
////todo track and manipulate selection here? idk data coming from vh would have to contain current position
//        if (strToReturn.startsWith(".")) {
//            strToReturn = "0$strToReturn"
//        }
//
//        val hasDecimal = strToReturn.contains('.')
//        var whole: String
//        var fraction: String
//
//        if (hasDecimal) {
//            strToReturn.split('.').let {
//                whole = it.first()
//                fraction = it.last()
//            }
//
//            // this clears front zeros, if all zeros are cleared, replace with "0"
//            if (whole.startsWith("0") && whole.length > 1) {
//                whole = whole.trimStart { it == '0' }.apply {
//                    if (this.isEmpty()) {
//                        plus("0")
//                    }
//                }
//            }
//
//            // sanitize fraction part
//            // LIMIT INPUT TO TWO LETTERS
//            fraction = fraction.take(2)
//
//            strToReturn = "$whole.$fraction"
//        } else {
//            // this clears front zeros, if all zeros are cleared, replace with "0"
//            if (strToReturn.startsWith("0") && strToReturn.length > 1) {
//                strToReturn = strToReturn.trimStart { it == '0' }
//                    .apply {
//                        if (this.isEmpty()) {
//                            plus("0")
//                        }
//                    }
//            }
//        }
//
//        return strToReturn
//    }

//    private fun prepareValueString(value: Double): String {
////        return value.roundTo2Decimals().toString().sanitizeBaseValue()
//        return value.roundTo2Decimals().toString()
//    }

//    private fun String.toValidDouble(): Double {
//        //todo add more cases so it doesn't start with 0 and .
//        var strToReturn = this
//        if (this.isBlank()) {
//            return 0.0
//        }
//        //todo this is an overkill too probably
//        if (this.endsWith(".")) {
//            strToReturn.trimEnd { it == '.' }
////            return this.dropLast(1).toDouble()
//        }
//        //todo this is not needed
//        if (!this.startsWith("0.") && this.startsWith("0")) {
//            strToReturn.trimStart { it == '0' }
////            return this.dropLast(1).toDouble()
//        }
//
//        return this.toDouble()
//    }

    override fun onCleared() {
        super.onCleared()
        disposeBag.clear()
    }
}