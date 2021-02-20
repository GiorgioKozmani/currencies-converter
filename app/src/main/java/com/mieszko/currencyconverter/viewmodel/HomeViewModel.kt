package com.mieszko.currencyconverter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mieszko.currencyconverter.common.SupportedCode
import com.mieszko.currencyconverter.data.model.HomeListModel
import com.mieszko.currencyconverter.data.model.Resource
import com.mieszko.currencyconverter.data.repository.ICodesDataRepository
import com.mieszko.currencyconverter.data.repository.IRatiosRepository
import com.mieszko.currencyconverter.data.repository.ITrackedCurrenciesRepository
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.net.UnknownHostException
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToLong


class HomeViewModel(
    private val ratiosRepository: IRatiosRepository,
    private val trackedCurrenciesRepository: ITrackedCurrenciesRepository,
    private val codesDataRepository: ICodesDataRepository,
) : ViewModel() {
    private val disposeBag = CompositeDisposable()

    private val currenciesListModelsLiveData: MutableLiveData<Resource<List<HomeListModel>>> =
        MutableLiveData()
    private val lastUpdatedLiveData: MutableLiveData<Resource<Date>> =
        MutableLiveData()

    // all available toUahRatios
    private lateinit var currenciesRatios: EnumMap<SupportedCode, Double>

    // models that are being passed to the view
    private var currenciesListModels = listOf<HomeListModel>()

    //Exposing only LiveData
    fun getCurrenciesLiveData(): LiveData<Resource<List<HomeListModel>>> =
        currenciesListModelsLiveData

    fun getLastUpdatedLiveData(): LiveData<Resource<Date>> =
        lastUpdatedLiveData

    //TODO IDEAS:
    // FOR CACHE: ONLY EMIT FROM CACHE IF THERE'S NO DATA FROM WEB. ALWAYS TRY TO GET NEW DATA ON APP START
    // TODO HANDLE ALL !!s
    // TODO ENTRY ANIMATION
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
            .subscribe(
                { emitModels() },
                {
                    //todo remember crashlitics
                    //TODO REVERT ERROR HANDLING AND RETRY
                    emitError(it)
                })
        )
    }

    private fun repositionRegularItems(trackedCurrencies: List<SupportedCode>) =
        Single.fromCallable {
            // Just change order of items if base hasn't changed
            trackedCurrencies
                .forEachIndexed { index, supportedCurrency ->
                    if (supportedCurrency != currenciesListModels[index].code) {
                        Collections.swap(
                            currenciesListModels,
                            index,
                            currenciesListModels.indexOfFirst { it.code == supportedCurrency }
                        )
                    }
                }
        }

    fun setBaseCurrencyAmount(newAmount: Double) {
        val baseToUahRatio = currenciesRatios[currenciesListModels.first().code]!!

        recreateCurrentItems(newAmount, baseToUahRatio)
            .subscribeOn(Schedulers.computation())
            .doOnSuccess { listItems -> currenciesListModels = listItems }
            .subscribe({ emitModels() },
                {
                    // todo add safesubscribe that is going to report to crashlitics non fatals
                })
    }

    private fun recreateCurrentItems(
        baseAmount: Double,
        baseToUahRatio: Double
    ): Single<List<HomeListModel>> =
        Single.fromCallable {
            currenciesListModels
                .map { currentItem ->
                    when (currentItem) {
                        is HomeListModel.Base -> currentItem.copy(amount = baseAmount)
                        is HomeListModel.Regular -> currentItem.copy(
                            amount = calculateCurrencyAmount(
                                currToUahRatio = currenciesRatios[currentItem.code]!!,
                                baseToUahRatio = baseToUahRatio,
                                baseAmount = baseAmount
                            )
                        )
                    }
                }
        }

    fun setBaseCurrency(newBaseCurrency: SupportedCode) {
        trackedCurrenciesRepository
            .moveTrackedCurrencyToTop(newBaseCurrency)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun moveItem(from: Int, to: Int) {
        trackedCurrenciesRepository
            .swapTrackingOrder(
                currenciesListModels[from].code,
                currenciesListModels[to].code
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

    private fun getSupportedCurrenciesRatios(): Single<EnumMap<SupportedCode, Double>> {
        //TODO REVISE CACHE FOR RATIOS
//            //todo as this is updated once in a day at 16, this would be nice to have it added as a (?) button, together with refresh button but don't update it every second
        return ratiosRepository
            .loadCurrenciesRatios()
            .map { currenciesResponse ->
                currenciesResponse.apply {
                    // todo document this part as it's not fully clear at first sight
                    put(SupportedCode.UAH, 1.0)
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

    private fun emitModels() {
        currenciesListModelsLiveData.postValue(Resource.Success(currenciesListModels))
    }

    private fun makeListItemModels(trackedCurrencies: List<SupportedCode>): Single<List<HomeListModel>> {
        var baseCurrency = currenciesListModels.firstOrNull()
        //todo rework, this has to come from some repo, + obtaining same item twice is pointless
            ?: HomeListModel.Base(
                trackedCurrencies.first(),
                codesDataRepository.getCodeData(trackedCurrencies.first()),
                DEFAULT_BASE_AMOUNT
            )

        return Single.fromCallable {
            trackedCurrencies
                .mapIndexed { index, supportedCurrency ->
                    val newCurrencyRatio = currenciesRatios[supportedCurrency]!!
                    val newCurrencyCodeData = codesDataRepository.getCodeData(supportedCurrency)
                    val baseRatio = currenciesRatios[baseCurrency.code]!!
                    val baseAmount = baseCurrency.amount

                    if (index == 0) {
                        HomeListModel.Base(
                            code = supportedCurrency,
                            codeData = newCurrencyCodeData,
                            amount = calculateCurrencyAmount(
                                currToUahRatio = newCurrencyRatio,
                                baseToUahRatio = baseRatio,
                                baseAmount = baseAmount
                            )
                        ).also { baseCurrency = it }
                    } else {
                        val baseCurrencyCode = baseCurrency.code.name
                        val newCurrencyCode = supportedCurrency.name

                        HomeListModel.Regular(
                            code = supportedCurrency,
                            codeData = newCurrencyCodeData,
                            amount = calculateCurrencyAmount(
                                currToUahRatio = newCurrencyRatio,
                                baseToUahRatio = baseRatio,
                                baseAmount = baseAmount
                            ),
                            baseToThisText = makeRatioString(
                                firstCurrencyCode = newCurrencyCode,
                                firstCurrencyToUahRatio = newCurrencyRatio,
                                secondCurrencyCode = baseCurrencyCode,
                                secondCurrencyToUahRatio = baseRatio
                            ),
                            thisToBaseText = makeRatioString(
                                firstCurrencyCode = baseCurrencyCode,
                                firstCurrencyToUahRatio = baseRatio,
                                secondCurrencyCode = newCurrencyCode,
                                secondCurrencyToUahRatio = newCurrencyRatio
                            )
                        )
                    }
                }
        }
    }

    private fun makeRatioString(
        firstCurrencyCode: String,
        firstCurrencyToUahRatio: Double,
        secondCurrencyCode: String,
        secondCurrencyToUahRatio: Double
    ) = if (secondCurrencyToUahRatio != 0.0) {
        "1 $firstCurrencyCode ≈ ${
            (firstCurrencyToUahRatio / secondCurrencyToUahRatio)
                .roundToDecimals(3)
        } $secondCurrencyCode"
    } else {
        //todo TO CRASHLYTICS
        ""
    }

    private fun Double.roundToDecimals(decimals: Int): Double {
        val factor = 10.0.pow(decimals)
        return (this * factor).roundToLong() / factor
    }

    private fun itemsDidNotChange(trackedCurrencies: List<SupportedCode>): Boolean =
        trackedCurrencies.size == currenciesListModels.size
                && trackedCurrencies.containsAll(currenciesListModels.map { it.code })

    private fun baseStaysTheSame(trackedCurrencies: List<SupportedCode>): Boolean =
        trackedCurrencies.first() == currenciesListModels.firstOrNull()?.code

    private fun clearListModels() = Single.fromCallable {
        currenciesListModels = listOf()
    }

    override fun onCleared() {
        super.onCleared()
        disposeBag.clear()
    }

    private companion object {
        const val DEFAULT_BASE_AMOUNT = 100.0
    }
}