package com.mieszko.currencyconverter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mieszko.currencyconverter.common.SupportedCurrency
import com.mieszko.currencyconverter.data.model.HomeListItem
import com.mieszko.currencyconverter.data.model.Resource
import com.mieszko.currencyconverter.data.repository.ICurrenciesRepository
import com.mieszko.currencyconverter.data.repository.ITrackedCurrenciesRepository
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.net.UnknownHostException
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToLong


class HomeViewModel(
    private val dataRepository: ICurrenciesRepository,
    private val trackedCurrenciesRepository: ITrackedCurrenciesRepository
) : ViewModel() {
    private val disposeBag = CompositeDisposable()

    private val currenciesListModelsLiveData: MutableLiveData<Resource<List<HomeListItem>>> =
        MutableLiveData()
    private val lastUpdatedLiveData: MutableLiveData<Resource<Date>> =
        MutableLiveData()

    // all available toUahRatios
    private lateinit var currenciesRatios: EnumMap<SupportedCurrency, Double>

    // models that are being passed to the view
    private var currenciesListModels = listOf<HomeListItem>()

    //Exposing only LiveData
    fun getCurrenciesLiveData(): LiveData<Resource<List<HomeListItem>>> =
        currenciesListModelsLiveData

    fun getLastUpdatedLiveData(): LiveData<Resource<Date>> =
        lastUpdatedLiveData

    //TODO IDEAS:
    // FOR CACHE: ONLY EMIT FROM CACHE IF THERE'S NO DATA FROM WEB. ALWAYS TRY TO GET NEW DATA ON APP START
    // TODO HANDLE ALL !!s
    init {
        disposeBag.add(getSupportedCurrenciesRatios()
            .subscribeOn(Schedulers.io())
            .doOnSuccess { newRatios -> currenciesRatios = newRatios }
            .flatMapObservable {
                trackedCurrenciesRepository
                    .getTrackedCurrencies()
                    .subscribeOn(Schedulers.io())
            }
            .flatMapSingle { trackedCurrencies ->
                when {
                    trackedCurrencies.isEmpty() -> {
                        clearListModels()
                            .subscribeOn(Schedulers.computation())
                    }
                    baseStaysTheSame(trackedCurrencies) && itemsDidNotChange(trackedCurrencies) -> {
                        repositionRegularItems(trackedCurrencies)
                            .subscribeOn(Schedulers.computation())
                    }
                    //todo add case for adding / removing some when base haven't changed!
                    else -> {
                        makeListItemModels(trackedCurrencies)
                            .doOnSuccess { listItems -> currenciesListModels = listItems }
                            .subscribeOn(Schedulers.computation())
                    }
                }
            }
            .subscribe {
                emitCurrencies()
                //todo handle error, remember crashlitics
            })
    }

    private fun repositionRegularItems(trackedCurrencies: List<SupportedCurrency>) =
        Single.fromCallable {
            // Just change order of items if base hasn't changed
            trackedCurrencies
                .forEachIndexed { index, supportedCurrency ->
                    if (supportedCurrency != currenciesListModels[index].currency) {
                        Collections.swap(
                            currenciesListModels,
                            index,
                            currenciesListModels.indexOfFirst { it.currency == supportedCurrency }
                        )
                    }
                }
        }

    fun setBaseCurrencyAmount(newAmount: Double) {
        val baseToUahRatio = currenciesRatios[currenciesListModels.first().currency]!!

        recreateCurrentItems(newAmount, baseToUahRatio)
            .subscribeOn(Schedulers.computation())
            .doOnSuccess { listItems -> currenciesListModels = listItems }
            .subscribe({ emitCurrencies() },
                {
                    // todo add safesubscribe that is going to report to crashlitics non fatals
                })
    }

    private fun recreateCurrentItems(
        baseAmount: Double,
        baseToUahRatio: Double
    ): Single<List<HomeListItem>> =
        Single.fromCallable {
            currenciesListModels
                .map { currentItem ->
                    when (currentItem) {
                        is HomeListItem.Base -> currentItem.copy(amount = baseAmount)
                        is HomeListItem.Regular -> currentItem.copy(
                            amount = calculateCurrencyAmount(
                                currToUahRatio = currenciesRatios[currentItem.currency]!!,
                                baseToUahRatio = baseToUahRatio,
                                baseAmount = baseAmount
                            )
                        )
                    }
                }
        }
            .subscribeOn(Schedulers.computation())

    fun setBaseCurrency(newBaseCurrency: SupportedCurrency) {
        trackedCurrenciesRepository
            .moveTrackedCurrencyToTop(newBaseCurrency)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun moveItem(from: Int, to: Int) {
        trackedCurrenciesRepository
            .swapTrackingOrder(
                currenciesListModels[from].currency,
                currenciesListModels[to].currency
            )
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    private fun calculateCurrencyAmount(
        currToUahRatio: Double,
        baseToUahRatio: Double,
        baseAmount: Double
    ) =
        try {
            (baseToUahRatio / currToUahRatio * baseAmount)
                .roundToDecimals(2)
        } catch (t: Throwable) {
            //todo TO CRASHLYTICS
            0.0
        }

    private fun getSupportedCurrenciesRatios(): Single<EnumMap<SupportedCurrency, Double>> {
        //TODO REVISE CACHE FOR RATIOS
//            //todo as this is updated once in a day at 16, this would be nice to have it added as a (?) button, together with refresh button but don't update it every second
        return dataRepository
            .loadCurrencies()
            .map { currenciesResponse ->
                currenciesResponse.apply {
                    // todo document this part as it's not fully clear at first sight
                    put(SupportedCurrency.UAH, 1.0)
                }
            }
    }

//    private fun emitUpdateDate() {
//        //todo revise
//     val dateLong = SharedPrefsManager.getLong(SharedPrefsManager.Key.CachedCurrenciesTime)
//     val dateResource =
//         if (dateLong != (-1).toLong()) {
//             Resource.Success(Date(dateLong))
//         } else {
//             Resource.Error("Never updated")
//         }
//     lastUpdatedLiveData.postValue(dateResource)
//    }

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
        currenciesListModelsLiveData.postValue(Resource.Success(currenciesListModels))
    }

    private fun makeListItemModels(trackedCurrencies: List<SupportedCurrency>): Single<List<HomeListItem>> {
        var baseCurrency = currenciesListModels.firstOrNull()
            ?: HomeListItem.Base(trackedCurrencies.first(), 0.0)

        return Single.fromCallable {
            trackedCurrencies
                .mapIndexed { index, supportedCurrency ->
                    val newCurrencyRatio = currenciesRatios[supportedCurrency]!!
                    val baseRatio = currenciesRatios[baseCurrency.currency]!!
                    val baseAmount = baseCurrency.amount

                    if (index == 0) {
                        HomeListItem.Base(
                            currency = supportedCurrency,
                            amount = calculateCurrencyAmount(
                                currToUahRatio = newCurrencyRatio,
                                baseToUahRatio = baseRatio,
                                baseAmount = baseAmount
                            )
                        ).also { baseCurrency = it }
                    } else {
                        val baseCurrencyName = baseCurrency.currency.name
                        val newCurrencyName = supportedCurrency.name

                        HomeListItem.Regular(
                            currency = supportedCurrency,
                            amount = calculateCurrencyAmount(
                                currToUahRatio = newCurrencyRatio,
                                baseToUahRatio = baseRatio,
                                baseAmount = baseAmount
                            ),
                            baseToThisText = makeRatioString(
                                firstCurrencyName = newCurrencyName,
                                firstCurrencyToUahRatio = newCurrencyRatio,
                                secondCurrencyName = baseCurrencyName,
                                secondCurrencyToUahRatio = baseRatio
                            ),
                            thisToBaseText = makeRatioString(
                                firstCurrencyName = baseCurrencyName,
                                firstCurrencyToUahRatio = baseRatio,
                                secondCurrencyName = newCurrencyName,
                                secondCurrencyToUahRatio = newCurrencyRatio
                            )
                        )
                    }
                }
        }
    }

    private fun makeRatioString(
        firstCurrencyName: String,
        firstCurrencyToUahRatio: Double,
        secondCurrencyName: String,
        secondCurrencyToUahRatio: Double
    ) = if (secondCurrencyToUahRatio != 0.0) {
        "1 $firstCurrencyName ≈ ${
            (firstCurrencyToUahRatio / secondCurrencyToUahRatio)
                .roundToDecimals(3)
        } $secondCurrencyName"
    } else {
        //todo TO CRASHLYTICS
        ""
    }

    private fun Double.roundToDecimals(decimals: Int): Double {
        val factor = 10.0.pow(decimals)
        return (this * factor).roundToLong() / factor
    }

    private fun itemsDidNotChange(trackedCurrencies: List<SupportedCurrency>): Boolean =
        trackedCurrencies.size == currenciesListModels.size
                && trackedCurrencies.containsAll(currenciesListModels.map { it.currency })

    private fun baseStaysTheSame(trackedCurrencies: List<SupportedCurrency>): Boolean =
        trackedCurrencies.first() == currenciesListModels.firstOrNull()?.currency

    private fun clearListModels() = Single.fromCallable {
        currenciesListModels = listOf()
    }

    override fun onCleared() {
        super.onCleared()
        disposeBag.clear()
    }
}