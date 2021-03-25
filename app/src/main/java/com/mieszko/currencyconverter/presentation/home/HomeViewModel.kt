package com.mieszko.currencyconverter.presentation.home

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mieszko.currencyconverter.common.base.BaseViewModel
import com.mieszko.currencyconverter.common.model.Resource
import com.mieszko.currencyconverter.common.model.SupportedCode
import com.mieszko.currencyconverter.common.util.IDisposablesBag
import com.mieszko.currencyconverter.domain.analytics.IFirebaseEventsLogger
import com.mieszko.currencyconverter.domain.analytics.events.BaseValueChangedEvent
import com.mieszko.currencyconverter.domain.model.CodeWithData
import com.mieszko.currencyconverter.domain.model.list.HomeListModel
import com.mieszko.currencyconverter.domain.usecase.IMapDataToCodesUseCase
import com.mieszko.currencyconverter.domain.usecase.ratios.IFetchRemoteRatiosUseCase
import com.mieszko.currencyconverter.domain.usecase.ratios.IObserveRatiosUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.IMoveTrackedCodeToTopUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.ISwapTrackedCodesUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud.IObserveTrackedCodesUseCase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.Subject
import java.math.BigDecimal
import java.math.MathContext
import java.net.UnknownHostException
import java.util.Date
import java.util.NoSuchElementException
import java.util.concurrent.TimeUnit
import kotlin.math.pow
import kotlin.math.roundToLong

// TODO
// https://proandroiddev.com/anemic-repositories-mvi-and-rxjava-induced-design-damage-and-how-aac-viewmodel-is-silently-1762caa70e13
// The take-away here is that if:
// you don’t have a saveState/restoreState function on your ViewModel
// you don’t have an initialState: Bundle? constructor argument on your ViewModel (passed in from the ViewModelProvider.Factory in your Activity/Fragment as a dynamic argument)
// you don’t use SavedStateHandle.getLiveData("key") nor SavedStateHandle.get("key")/set("key")
// https://developer.android.com/jetpack/androidx/releases/lifecycle#version_230_3
// there are some changes to saving state made recently

class HomeViewModel(
    disposablesBag: IDisposablesBag,
    observeRatiosUseCase: IObserveRatiosUseCase,
    observeTrackedCodesUseCase: IObserveTrackedCodesUseCase,
    private val fetchRemoteRatiosUseCase: IFetchRemoteRatiosUseCase,
    private val moveTrackedCodeToTopUseCase: IMoveTrackedCodeToTopUseCase,
    private val swapTrackedCodesUseCase: ISwapTrackedCodesUseCase,
    private val mapCodeToDataUseCase: IMapDataToCodesUseCase,
    private val eventsLogger: IFirebaseEventsLogger
) : BaseViewModel(disposablesBag) {

    private val currenciesListModelsLiveData: MutableLiveData<Resource<List<HomeListModel>>> =
        MutableLiveData()
    private val lastUpdatedLiveData: MutableLiveData<Date> =
        MutableLiveData()
    private val isLoadingLiveData: MutableLiveData<Boolean> =
        MutableLiveData()

    // Exposing only LiveData
    fun getCurrenciesLiveData(): LiveData<Resource<List<HomeListModel>>> =
        currenciesListModelsLiveData

    fun getLastUpdatedLiveData(): LiveData<Date> =
        lastUpdatedLiveData

    fun getIsLoadingLiveData(): LiveData<Boolean> =
        isLoadingLiveData

    private val baseAmountChange: Subject<Double> =
        BehaviorSubject.createDefault(DEFAULT_BASE_AMOUNT)

    // TODO HANDLE ALL !!s
    private var currenciesListModels = listOf<HomeListModel>()

    init {
        // emit list items when ratios or tracking order or base amount changes
        setupListDataSource(observeRatiosUseCase, observeTrackedCodesUseCase)

        // ATTEMPT FETCHING FRESH DATA FROM NETWORK
        fetchRemoteRatios()

        setupBaseValueChangeLogging()
    }

    private fun setupBaseValueChangeLogging() {
        disposablesBag.add(
            baseAmountChange
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeBy(onNext = { baseValue ->
                    eventsLogger.logEvent(
                        BaseValueChangedEvent(
                            currenciesListModels.first().code,
                            baseValue
                        )
                    )
                })
        )
    }

    private fun setupListDataSource(
        observeRatiosUseCase: IObserveRatiosUseCase,
        observeTrackedCodesUseCase: IObserveTrackedCodesUseCase
    ) {
        disposablesBag.add(
            // todo use RxRelay with default of loading?
            Observable.combineLatest(
                // CODES RATIOS CHANGED
                Observable.combineLatest(
                    observeRatiosUseCase()
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.computation())
                        .doOnNext { lastUpdatedLiveData.postValue(it.time) },
                    // TRACKING CODES CHANGED
                    observeTrackedCodesUseCase()
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.computation()),
                    { allRatios, trackedCodes ->
                        mapCodeToDataUseCase(codes = trackedCodes, allRatios = allRatios.ratios)
                    }
                )
                    // TODO STEP AWAY FROM PAIR
                    .map { newAmount -> Pair(System.nanoTime(), newAmount) },
                // USER INPUT NEW BASE CURRENCY
                baseAmountChange
                    .subscribeOn(Schedulers.computation())
                    .observeOn(Schedulers.computation())
                    .map { newAmount -> Pair(System.nanoTime(), newAmount) },
                { currenciesChange, newBaseAmount ->
                    val trackedCodesWithData = currenciesChange.second

                    if (trackedCodesWithData.isEmpty()) {
                        listOf()
                    } else {
                        // ratios or tracking order changed
                        if (currenciesChange.first > newBaseAmount.first) {
                            makeListItemModels(
                                baseAmount = currenciesListModels.find {
                                    it.code == trackedCodesWithData.first().code
                                }
                                    ?.amount
                                    ?: DEFAULT_BASE_AMOUNT,
                                trackedCodesWithData = trackedCodesWithData
                            )
                            // base amount change by user
                        } else {
                            val baseAmount = newBaseAmount.second

                            makeListItemModels(
                                baseAmount = baseAmount,
                                trackedCodesWithData = trackedCodesWithData
                            )
                        }
                    }
                }
            )
                .doOnNext { listItems -> currenciesListModels = listItems }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onNext = { listItems ->
                        emitModels(listItems)
                    },
                    onError = {
                        eventsLogger.logNonFatalError(it, "HOME LIST DATA SOURCE ERROR")
                        emitError(it)
                    }
                )
        )
    }

    fun refreshButtonClicked() {
        fetchRemoteRatios()
    }

    private fun fetchRemoteRatios() {
        disposablesBag.add(
            fetchRemoteRatiosUseCase()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { isLoadingLiveData.value = true }
                .doOnTerminate { isLoadingLiveData.value = false }
                .subscribeBy(onError = {
                    eventsLogger.logNonFatalError(it, "REMOTE RATIOS REQUEST FAILED")
                    emitError(it)
                }
                )
        )
    }

    fun baseCurrencyAmountChanged(newAmount: Double) {
        baseAmountChange.onNext(newAmount)
    }

    fun listItemClicked(newBaseCurrency: SupportedCode) {
        // ignore base item clicks
        if (currenciesListModels.isNotEmpty() && currenciesListModels.first().code != newBaseCurrency) {
            moveTrackedCodeToTopUseCase(newBaseCurrency)
                .subscribeOn(Schedulers.io())
                .subscribe()
        }
    }

    fun itemDragged(from: Int, to: Int) {
        swapTrackedCodesUseCase(
            currenciesListModels[from].code,
            currenciesListModels[to].code
        )
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    @WorkerThread
    private fun calculateCurrencyAmount(
        currToUahRatio: Double,
        baseToUahRatio: Double,
        baseAmount: Double
    ) =
        try {
            (baseToUahRatio / currToUahRatio * baseAmount)
                .roundToDecimals(2)
        } catch (t: Throwable) {
            // todo TO CRASHLYTICS
            0.0
        }

    // TODO REWORK
    private fun emitError(t: Throwable?) {
        // todo strings, revise

        // REVISE
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

    private fun emitModels(listItemsModels: List<HomeListModel>) {
        // todo step away from emitting resource from here
        currenciesListModelsLiveData.value = Resource.Success(listItemsModels)
    }

    @WorkerThread
    private fun makeListItemModels(
        baseAmount: Double,
        trackedCodesWithData: List<CodeWithData>
    ): List<HomeListModel> {
        val baseCode = trackedCodesWithData.first().code
        val baseToUahRatio = trackedCodesWithData.first().toUahRatio

        return trackedCodesWithData.mapIndexed { index, trackedCodeWithData ->
            val newCurrencyCode = trackedCodeWithData.code
            val newCurrencyRatio = trackedCodeWithData.toUahRatio
            val newCurrencyData = trackedCodeWithData.staticData

            if (index == 0) {
                HomeListModel.Base(
                    code = baseCode,
                    codeStaticData = newCurrencyData,
                    amount = baseAmount
                )
            } else {
                HomeListModel.NonBase(
                    code = newCurrencyCode,
                    codeStaticData = newCurrencyData,
                    amount = calculateCurrencyAmount(
                        currToUahRatio = newCurrencyRatio,
                        baseToUahRatio = baseToUahRatio,
                        baseAmount = baseAmount
                    ),
                    baseToThisText = makeRatioString(
                        firstCurrencyCode = newCurrencyCode,
                        firstCurrencyToUahRatio = newCurrencyRatio,
                        secondCurrencyCode = baseCode,
                        secondCurrencyToUahRatio = baseToUahRatio
                    ),
                    thisToBaseText = makeRatioString(
                        firstCurrencyCode = baseCode,
                        firstCurrencyToUahRatio = baseToUahRatio,
                        secondCurrencyCode = newCurrencyCode,
                        secondCurrencyToUahRatio = newCurrencyRatio
                    )
                )
            }
        }
    }

    // todo extract to usecase
    @WorkerThread
    private fun makeRatioString(
        firstCurrencyCode: SupportedCode,
        firstCurrencyToUahRatio: Double,
        secondCurrencyCode: SupportedCode,
        secondCurrencyToUahRatio: Double
    ) = if (secondCurrencyToUahRatio != 0.0) {
        getReadableRatio(firstCurrencyToUahRatio, secondCurrencyToUahRatio).let {
            "${it.first} $firstCurrencyCode ≈ ${it.second.roundToDecimals(3)} $secondCurrencyCode"
        }

    } else {
        // todo TO CRASHLYTICS
        ""
    }

    private fun getReadableRatio(
        firstCurrencyToUahRatio: Double,
        secondCurrencyToUahRatio: Double
    ): Pair<Int, Double> {
        var roundedRatio = BigDecimal(firstCurrencyToUahRatio / secondCurrencyToUahRatio)
        // round to 3 significant figures
        roundedRatio = roundedRatio.round(MathContext(3))

        return prettifyRatio(Pair(1, roundedRatio.toDouble()))
    }

    // TODO MOVE TO USECASE!
    /**
     * Recursive method that multiplies both Pair(A: Int, B: Double) values by 10
     * until B >= 0.01
     * for example Pair(1, 0.00001) into Pair(100, 0.001)
     */
    private fun prettifyRatio(pair: Pair<Int, Double>): Pair<Int, Double> {
        return if (pair.second < 0.01) {
            prettifyRatio(Pair(pair.first * 10, pair.second * 10))
        } else {
            pair
        }
    }

    @WorkerThread
    private fun Double.roundToDecimals(decimals: Int): Double {
        val factor = 10.0.pow(decimals)
        return (this * factor).roundToLong() / factor
    }

    private companion object {
        const val DEFAULT_BASE_AMOUNT = 100.0
    }
}
