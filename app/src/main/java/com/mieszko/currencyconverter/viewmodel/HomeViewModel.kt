package com.mieszko.currencyconverter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mieszko.currencyconverter.common.SupportedCurrency
import com.mieszko.currencyconverter.data.model.CurrencyRatio
import com.mieszko.currencyconverter.data.model.HomeListItem
import com.mieszko.currencyconverter.data.model.Resource
import com.mieszko.currencyconverter.data.repository.ICurrenciesRepository
import com.mieszko.currencyconverter.data.repository.ITrackedCurrenciesRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.net.UnknownHostException
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToLong


class HomeViewModel(private val dataRepository: ICurrenciesRepository, private val trackedCurrenciesRepository: ITrackedCurrenciesRepository) : ViewModel() {
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

    init {
    //todo dispose on detach
        trackedCurrenciesRepository
            .getTrackedCurrencies()
            .subscribeOn(Schedulers.io())
            .subscribe {
                //todo or think of different approach that this would be only used for reordering
                //todo this will require rework
                // todo store amounts of all
                trackedCurrenciesList = it.toMutableList()

                getAllRates()
            }
    }

    fun setBaseCurrencyAmount(newAmount: Double) {
        //todo i can also look for an instance
        currenciesListModels.first().apply { amount = newAmount }
        emitCurrencies()
    }

    // Move new base currency to the top of the list, and inform adapter about the change so it's instantly animated
    fun setBaseCurrency(newBaseCurrency: SupportedCurrency) {
        moveBaseCurrencyTop(newBaseCurrency)
        currenciesListModels.first { it.currency == newBaseCurrency }
        emitCurrencies()
    }

    private fun moveBaseCurrencyTop(newBaseCurrency: SupportedCurrency) {
        trackedCurrenciesList.remove(newBaseCurrency)
        trackedCurrenciesList.add(0, newBaseCurrency)

        val listModel = currenciesListModels.first { it.currency == newBaseCurrency }
        currenciesListModels.remove(listModel)
        currenciesListModels.add(0, listModel)
    }

    fun moveItem(from: Int, to: Int) {
        Completable.fromAction {
            Collections.swap(trackedCurrenciesList, from, to)
            Collections.swap(currenciesListModels, from, to)
        }
            .subscribeOn(Schedulers.io())
            .doOnComplete {
                if (to == 0 || from == 0) {
                    // recreate items on base change
                    emitCurrencies()
                } else {
                    // just emit order changing
                    currenciesListModelsLiveData.postValue(Resource.Success(currenciesListModels))
                }
            }
            .subscribe()
    }

    private fun calculateCurrencyAmount(currToUahRatio: Double, baseAmount: Double) =
        try {
            //TODO THIS IS EXTREMELY BAD, LIFT UP, RETHING
            (currenciesRatios.find { it.currency == trackedCurrenciesList[0] }!!.toUAHRatio / currToUahRatio * baseAmount).roundToDecimals(
                2
            )
        } catch (t: Throwable) {
            //todo TO CRASHLYTICS
            0.0
        }

    private fun getAllRates() {
        //TODO REVISE CACHE FOR RATIOS
        disposeBag.add(
//            //todo as this is updated once in a day at 16, this would be nice to have it added as a (?) button, together with refresh button but don't update it every second
            dataRepository
                .loadCurrencies()
                .subscribeOn(Schedulers.io())
                .subscribe({ currenciesResponse ->
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

                    emitItemsAndUpdateTime()
                }, { t -> emitError(t) })
        )
    }

    // Handled in the background thread, in order to avoid blocking UI thread by the mapping,
    // hence postValue, not setValue is used
    private fun emitItemsAndUpdateTime() {
        // Posts a task to a main thread to set the given value.
        // If this method is called multiple times before a main thread executed a posted task,
        // only the last value would be dispatched.
        emitCurrencies()
        emitUpdateDate()
    }

    private fun emitUpdateDate() {
        //todo revise

        //todo revise

//        val dateLong = SharedPrefsManager.getLong(SharedPrefsManager.Key.CachedCurrenciesTime)
//        val dateResource =
//            if (dateLong != (-1).toLong()) {
//                Resource.Success(Date(dateLong))
//            } else {
//                Resource.Error("Never updated")
//            }
//        lastUpdatedLiveData.postValue(dateResource)
    }

    private fun emitError(t: Throwable?) {
        //todo strings, revise
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

    private fun emitCurrencies() {
        getListItemModels()
            .subscribeOn(Schedulers.io())
            .subscribe({ values -> currenciesListModelsLiveData.postValue(Resource.Success(values)) },
                {
                    // handle model creation error
                })
//            .subscribe { values ->
//                currenciesListModelsLiveData.postValue(Resource.Success(values))
//            }
    }

    private fun getListItemModels(): Single<List<HomeListItem>> {
        return Single.just(
            trackedCurrenciesList.map { trackedCurrency ->
                //todo this would be nice to be able to use some map with currency / name as key
                currenciesRatios.find { it.currency == trackedCurrency }
            }.mapIndexedNotNull { index, currencyRatio ->
                // todo don't try to obtain first object always
                //TODO THIS WILL OFTEN FAIL AS TRACKED CURRENCIESLIST MIGHT BE EMPTY!! HANDLE THIS CASE
                val baseCurrency = currenciesListModels.firstOrNull() ?: HomeListItem.Base(
                    trackedCurrenciesList.first(),
                    100.0
                )

                // todo remove all !! s
                if (index == 0) {
                    HomeListItem.Base(
                        currency = currencyRatio!!.currency,
                        amount = baseCurrency.amount
                    )
                } else {
                    val baseRatio = currenciesRatios.find { it.currency == baseCurrency.currency }!!

                    HomeListItem.Regular(
                        currency = currencyRatio!!.currency,
                        amount = calculateCurrencyAmount(
                            currToUahRatio = currencyRatio.toUAHRatio,
                            baseAmount = baseCurrency.amount
                        ),
                        baseToThisText = makeRatioString(currencyRatio, baseRatio),
                        thisToBaseText = makeRatioString(baseRatio, currencyRatio)
                    )
                }
            })
            .doOnSuccess {
                //todo if size == 0 then controller should show some item prompting to select more
                currenciesListModels.clear()
                currenciesListModels.addAll(it)
            }
    }

    private fun makeRatioString(
        firstCurrency: CurrencyRatio,
        secondCurrency: CurrencyRatio
    ) = if (secondCurrency.toUAHRatio != 0.0) {
        "1 ${firstCurrency.currency.name} ≈ ${
            (firstCurrency.toUAHRatio / secondCurrency.toUAHRatio).roundToDecimals(
                3
            )
        } ${secondCurrency.currency.name}"
    } else {
        //todo TO CRASHLYTICS
        ""
    }

    private fun Double.roundToDecimals(decimals: Int): Double {
        val factor = 10.0.pow(decimals)
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