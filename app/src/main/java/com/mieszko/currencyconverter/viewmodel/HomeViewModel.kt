package com.mieszko.currencyconverter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mieszko.currencyconverter.common.SupportedCurrency
import com.mieszko.currencyconverter.data.model.CurrencyRatio
import com.mieszko.currencyconverter.data.model.HomeListItem
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


class HomeViewModel(private val dataRepository: ICurrenciesRepository) : ViewModel() {
    private val disposeBag = CompositeDisposable()

    private val currenciesListModelsLiveData: MutableLiveData<Resource<List<HomeListItem>>> =
        MutableLiveData()
    private val lastUpdatedLiveData: MutableLiveData<Resource<Date>> =
        MutableLiveData()

    // all data of supported currencies ratios
    private var currenciesRatios = mutableListOf<CurrencyRatio>()

    // models that are being passed to the view
    private var currenciesListModels = mutableListOf<HomeListItem>()

    //todo rethink mutability

    // list of tracked currencies to be displayed in proper order
    private lateinit var trackedCurrenciesList: MutableList<SupportedCurrency>

    //Exposing only LiveData
    fun getCurrenciesLiveData(): LiveData<Resource<List<HomeListItem>>> =
        currenciesListModelsLiveData

    fun getLastUpdatedLiveData(): LiveData<Resource<Date>> =
        lastUpdatedLiveData

    //    private lateinit var baseCurrencyData: CurrencyRatio
//    private lateinit var baseCurrency: CurrencyRatio
//    private var baseCurrencyAmount: Double = 0.0

    init {
//        setupSupportedCurrencies()
        // todo not really! it should select first from the selections list instead!
//        setBaseCurrency(SupportedCurrency.UAH)
        //TODO GET FROM SP INSTEAD
        trackedCurrenciesList = SupportedCurrency.values().toMutableList()
        getAllRates()
    }


    //TODO IDEA: SOME MAP OF CURRENCY -> VALUE
    fun setBaseCurrencyAmount(newAmount: Double) {
//        Completable.fromAction {
//            if (newAmount != baseCurrencyData.amount) {
//                baseCurrencyData.amount = newAmount
//                currenciesDataList.forEach { supportedCurrencyData ->
//                    if (supportedCurrencyData != baseCurrencyData) {
//                        supportedCurrencyData.amount =
//                            calculateCurrencyAmount(supportedCurrencyData.toUAHRatio)
//                    }
//                }
//                emitLiveData()  Bogdann1  604936794
//            }
//        }
        //todo this might be problematic, debug if lack of this check is not causing issues
//        if (newAmount != baseCurrencyAmount) {
        emitLiveData(newAmount)
//        }
    }

    // Move new base currency to the top of the list, and inform adapter about the change so it's instantly animated
    //todo pass string instead?
    //todo THINK IF IT MAKES SENSE TO UPDATE VALUES AFTER CHANGE, IMO IT DOESN'T AS WE KEEP TRACK ON SELECTION AND THIS IS ONLY THING WE SHOULD CARE ABOUT UNTIL BASE VALUE CHANGES
    fun setBaseCurrency(newBaseCurrency: SupportedCurrency) {
        moveBaseCurrencyTop(newBaseCurrency)
        emitCurrencies(currenciesListModels.first { it.currency == newBaseCurrency }.amount)
    }

    private fun moveBaseCurrencyTop(newBaseCurrency: SupportedCurrency) {
        //todo to bg thread?
        trackedCurrenciesList.remove(newBaseCurrency)
        trackedCurrenciesList.add(0, newBaseCurrency)
    }

    fun moveItem(from: Int, to: Int) {
        //TODO OCZYWISCIE OPTYMALIACJA TEGO GOWNA, ALE!!
        // TODO UPDATE ALL TTB AND BTT TEXTS WHEN BASE IS CHANGED!

        //TODO UPDATE:
        // if from or to is 0 then :
        // - save new base amount, use it for calculations for generating items
        // if any is NOT 0
        // - don't update values, don't recreate anything !!, just change order and emit like below
        Completable.fromAction {
            Collections.swap(trackedCurrenciesList, from, to)
            Collections.swap(currenciesListModels, from, to)
        }
            .subscribeOn(Schedulers.io())
            .doOnComplete {
                //todo clean up
                // recreate items on base change
                if (to == 0 || from == 0) {
                    emitCurrencies(currenciesListModels[0].amount)
                }else{
                    // just emit order changing
                    currenciesListModelsLiveData.postValue(Resource.Success(currenciesListModels))
                }
            }
            .subscribe()
    }

    private fun calculateCurrencyAmount(currToUahRatio: Double, baseAmount: Double) =
        try {
            //TODO THIS IS EXTREMELY BAD, LIFT UP, RETHING
            (currenciesRatios.find { it.currency == trackedCurrenciesList[0] }!!.toUAHRatio / currToUahRatio * baseAmount).roundTo2Decimals()
        } catch (t: Throwable) {
            //todo TO CRASHLYTICS
            0.0
        }

    private fun getAllRates() {
        disposeBag.add(
//            //todo as this is updated once in a day at 16, this would be nice to have it added as a (?) button, together with refresh button but don't update it every second
            dataRepository
                .loadCurrencies()
                .subscribeOn(Schedulers.io())
                .subscribe({ currenciesResponse ->
                    //todo simplify it, it might be better to do this other way around, and eliminate nulls
                    currenciesResponse
                        .mapNotNullTo(currenciesRatios) {
                            try {
                                CurrencyRatio(
                                    currency = SupportedCurrency.valueOf(it.shortName),
                                    toUAHRatio = it.ratioToUAH
                                )

                            } catch (t: Throwable) {
                                null
                            }
                        }
                        // add UAH as this one we're not getting from data source (for now, to be changed when aggregate data source will be implemented)
                        .add(CurrencyRatio(SupportedCurrency.UAH, 1.0))
                    //todo rethink
                    emitLiveData(DEFAULT_BASE_CURRENCY_AMOUNT)
                }, { t -> emitError(t) }
                )
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
    private fun emitLiveData(baseAmount: Double) {
        // Posts a task to a main thread to set the given value.
        // If this method is called multiple times before a main thread executed a posted task,
        // only the last value would be dispatched.
        emitCurrencies(baseAmount)
        //todo this is not currect as data source stays the same
        emitUpdateDate()
    }

    private fun emitUpdateDate() {
        val dateLong = SharedPrefs.getLong(SharedPrefs.Key.CachedCurrenciesTime)
        val dateResource =
            if (dateLong != (-1).toLong()) {
                Resource.Success(Date(dateLong))
            } else {
                Resource.Error("Never updated")
            }
        lastUpdatedLiveData.postValue(dateResource)
    }

    private fun emitCurrencies(baseAmount: Double) {
        getListItemModels(baseAmount)
            .subscribeOn(Schedulers.io())
            .subscribe { values ->
                currenciesListModelsLiveData.postValue(Resource.Success(values))
            }
    }

    private fun getListItemModels(baseAmount: Double): Single<List<HomeListItem>> {
        return Single.just(
            trackedCurrenciesList.map { trackedCurrency ->
                //todo this would be nice to be able to use some map with currency / name as key
                currenciesRatios.find { it.currency == trackedCurrency }
            }.mapIndexedNotNull { index, currencyRatio ->
                //todo this is uneficcient
                if (index == 0) {
                    HomeListItem.Base(
                        currency = currencyRatio!!.currency,
                        amount = baseAmount
                    )
                } else {
                    HomeListItem.Regular(
                        currency = currencyRatio!!.currency,
                        //todo verify
                        //todo trim last zeroes?
                        amount = calculateCurrencyAmount(
                            currToUahRatio = currencyRatio.toUAHRatio,
                            baseAmount = baseAmount
                        ),
                        //TODO REVERT CACHE
                        // todo this is very bad, optimise getting current base
                        baseToThisText = makeRatioString(
                            currencyRatio,
                            currenciesRatios.find { it.currency == trackedCurrenciesList[0] }!!
                        ),
                        thisToBaseText = makeRatioString(
                            currenciesRatios.find { it.currency == trackedCurrenciesList[0] }!!,
                            currencyRatio
                        )
                    )
                }
            })
            .doOnSuccess {
                currenciesListModels.clear()
                currenciesListModels.addAll(it)
            }
        //todo this is waste of resources
        //todo hashmap with key as currency?
    }

    private fun makeRatioString(
        firstCurrency: CurrencyRatio,
        secondCurrency: CurrencyRatio
    ) = if (secondCurrency.toUAHRatio != 0.0) {
        "1 ${firstCurrency.currency.name} ≈ ${(firstCurrency.toUAHRatio / secondCurrency.toUAHRatio).roundTo2Decimals()} ${secondCurrency.currency.name}"
    } else {
        //todo TO CRASHLYTICS
        ""
    }

    private fun Double.roundTo2Decimals(): Double {
        val factor = 10.0.pow(2)
        return (this * factor).roundToLong() / factor
    }

    override fun onCleared() {
        super.onCleared()
        disposeBag.clear()
    }

    companion object {
        const val DEFAULT_BASE_CURRENCY_AMOUNT = 100.0
    }
}