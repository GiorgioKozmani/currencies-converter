package com.mieszko.currencyconverter.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mieszko.currencyconverter.data.model.CurrencyListItemModel
import com.mieszko.currencyconverter.data.model.CurrencyModel
import com.mieszko.currencyconverter.data.model.Resource
import com.mieszko.currencyconverter.data.persistance.SharedPrefs
import com.mieszko.currencyconverter.data.repository.ICurrenciesRepository
import com.mieszko.currencyconverter.common.SupportedCurrency
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.net.UnknownHostException
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.pow
import kotlin.math.roundToLong


class CurrenciesViewModel(private val dataRepository: ICurrenciesRepository) : ViewModel() {
    private val disposeBag = CompositeDisposable()

    private val currenciesLiveData: MutableLiveData<Resource<List<CurrencyListItemModel>>> =
        MutableLiveData()
    private val lastUpdatedLiveData: MutableLiveData<Resource<Date>> =
        MutableLiveData()

    private var currenciesList = mutableListOf<CurrencyModel>()

    //Exposing only LiveData
    fun getCurrenciesLiveDate(): LiveData<Resource<List<CurrencyListItemModel>>> =
        currenciesLiveData

    fun getLastUpdatedLiveData(): LiveData<Resource<Date>> =
        lastUpdatedLiveData

    private lateinit var baseCurrencyModel: CurrencyModel

    init {
        setupSupportedCurrencies()
        setBaseCurrency(SupportedCurrency.EUR)
        startUpdatingRates()
    }

    fun setBaseCurrencyAmount(newAmount: Double) {
        baseCurrencyModel.amount = newAmount
        currenciesList
            .forEach { supportedCurrency ->
                if (supportedCurrency != baseCurrencyModel) {
                    supportedCurrency.amount =
                        calculateCurrencyAmount(supportedCurrency)
                }
            }
        emitLiveData()
    }

    //Move new base currency to the top of the list, and inform adapter about the change so it's instantly animated
    fun setBaseCurrency(newBaseCurrency: SupportedCurrency) {
        baseCurrencyModel = currenciesList.find { it.currency == newBaseCurrency }!!
        moveBaseCurrencyTop()
        emitLiveData()
    }

    fun moveItem(from: Int, to: Int) {
        val fromItem = currenciesList[from]
        currenciesList.removeAt(from)
        if (to < from) {
            currenciesList.add(to, fromItem)
        } else {
            currenciesList.add(to - 1, fromItem)
        }
        emitCurrencies()
    }

    private fun moveBaseCurrencyTop() {
        currenciesList.remove(baseCurrencyModel)
        currenciesList.add(0, baseCurrencyModel)
    }

    private fun startUpdatingRates() {
        disposeBag.add(
            //todo as this is updated once in a day at 16, this would be nice to have it added as a (?) button, together with refresh button but don't update it every second
            Observable.interval(1, TimeUnit.SECONDS)
                // Thanks to liveData.postValue, we can move whole flow to the background thread.
                .observeOn(Schedulers.io())
                .retry()
                .subscribe(
                    {
                        dataRepository.loadCurrencies()
                            .subscribe(
                                { currenciesResponse ->
                                    currenciesResponse
                                        .rates
                                        .forEach { singleCurrencyResponse ->
                                            //I have to do all below as we need to associate the hardcoded flagUrl and currencyName with api response items
                                            currenciesList
                                                .find { it.currency.name == singleCurrencyResponse.shortName }
                                                ?.apply {
                                                    toEuroRatio = singleCurrencyResponse.ratioToBase
                                                    if (this != baseCurrencyModel) {
                                                        amount = calculateCurrencyAmount(this)
                                                    }
                                                }
                                        }

                                    currenciesList
                                        .find { it.currency == SupportedCurrency.EUR }
                                        ?.apply {
                                            amount = calculateCurrencyAmount(this)
                                        }

                                    emitLiveData()
                                },
                                { t ->
                                    emitError(t)
                                }
                            )
                    },
                    { t ->
                        emitError(t)
                    }
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
        currenciesLiveData.postValue(Resource.Error(errorMessage))
    }

    private fun calculateCurrencyAmount(supportedCurrency: CurrencyModel) =
        (supportedCurrency.toEuroRatio / baseCurrencyModel.toEuroRatio * baseCurrencyModel.amount)
            .roundTo2Decimals()

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
                Resource.Error<Date>("Never updated")
            }
        lastUpdatedLiveData.postValue(dateResource)
    }

    private fun emitCurrencies() {
        currenciesLiveData.postValue(
            Resource.Success(getListItemModels())
        )
    }

    private fun getListItemModels(): List<CurrencyListItemModel> {
        return currenciesList
            .map { currencyModel ->
                CurrencyListItemModel(
                    currencyModel.currency,
                    currencyModel.amount
                )
            }
    }

    private fun setupSupportedCurrencies() {
        SupportedCurrency
            .values()
            .mapTo(currenciesList) { currency ->
                CurrencyModel(currency)
                    .apply {
                        if (currency == SupportedCurrency.EUR) {
                            toEuroRatio = 1.0; amount = 100.0
                        }
                    }
            }
    }

    private fun Double.roundTo2Decimals(): Double {
        val factor = 10.0.pow(2)
        return (this * factor).roundToLong() / factor
    }

    override fun onCleared() {
        super.onCleared()
        disposeBag.clear()
    }
}