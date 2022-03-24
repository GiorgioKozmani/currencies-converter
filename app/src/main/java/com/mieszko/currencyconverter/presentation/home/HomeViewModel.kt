package com.mieszko.currencyconverter.presentation.home

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mieszko.currencyconverter.common.base.BaseViewModel
import com.mieszko.currencyconverter.common.model.IDisposablesBag
import com.mieszko.currencyconverter.common.model.Resource
import com.mieszko.currencyconverter.common.model.SupportedCode
import com.mieszko.currencyconverter.domain.analytics.IFirebaseEventsLogger
import com.mieszko.currencyconverter.domain.analytics.events.BaseValueChangedEvent
import com.mieszko.currencyconverter.domain.analytics.events.ButtonClickedEvent
import com.mieszko.currencyconverter.domain.model.CodeWithData
import com.mieszko.currencyconverter.domain.model.DataUpdatedTime
import com.mieszko.currencyconverter.domain.model.list.HomeListItemModel
import com.mieszko.currencyconverter.domain.usecase.mappers.IMapDataToCodesUseCase
import com.mieszko.currencyconverter.domain.usecase.ratios.IFetchRemoteRatiosUseCase
import com.mieszko.currencyconverter.domain.usecase.ratios.IMakeRatioStringUseCase
import com.mieszko.currencyconverter.domain.usecase.ratios.IObserveRatiosUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.IMoveTrackedCodeToTopUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.ISwapTrackedCodesUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud.IObserveTrackedCodesUseCase
import com.mieszko.currencyconverter.domain.util.roundToDecimals
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

class HomeViewModel(
    disposablesBag: IDisposablesBag,
    observeRatios: IObserveRatiosUseCase,
    observeTrackedCodes: IObserveTrackedCodesUseCase,
    private val fetchRemoteRatios: IFetchRemoteRatiosUseCase,
    private val moveTrackedCodeToTop: IMoveTrackedCodeToTopUseCase,
    private val swapTrackedCodes: ISwapTrackedCodesUseCase,
    private val mapCodesToData: IMapDataToCodesUseCase,
    private val makeRatioString: IMakeRatioStringUseCase,
    private val eventsLogger: IFirebaseEventsLogger
) : BaseViewModel(disposablesBag) {

    private val currenciesListModelsLiveData: MutableLiveData<Resource<List<HomeListItemModel>>> =
        MutableLiveData()
    private val showEmptyStateLiveData: MutableLiveData<Boolean> =
        MutableLiveData()
    private val lastUpdatedLiveData: MutableLiveData<DataUpdatedTime> =
        MutableLiveData()
    private val isLoadingLiveData: MutableLiveData<Boolean> =
        MutableLiveData()

    private val baseAmountChange = BehaviorSubject.createDefault(DEFAULT_BASE_AMOUNT)
    private var currenciesListModels = listOf<HomeListItemModel>()

    init {
        // Emit list items when ratios or tracking order or base amount changes
        setupListDataSource(observeRatios, observeTrackedCodes)

        // Attempt fetching fresh data from network (this doesn't trigger any UI updates directly)
        fetchRatiosFromNetwork()

        setupBaseValueChangeLogging()
    }

    private fun setupBaseValueChangeLogging() {
        addSubscription(
            baseAmountChange
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .debounce(500, TimeUnit.MILLISECONDS)
                .subscribeBy(
                    onNext = { baseValue ->
                        currenciesListModels.firstOrNull()?.run {
                            eventsLogger.logEvent(BaseValueChangedEvent(code, baseValue))
                        }
                    }
                )
        )
    }

    private fun setupListDataSource(
        observeRatiosUseCase: IObserveRatiosUseCase,
        observeTrackedCodesUseCase: IObserveTrackedCodesUseCase
    ) {
        addSubscription(
            makeListItems(observeRatiosUseCase, observeTrackedCodesUseCase)
                .subscribeOn(Schedulers.io())
                .doOnNext { listItems -> assignAndEmitModels(listItems) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                    onError = {
                        eventsLogger.logNonFatalError(it, "HOME LIST DATA SOURCE ERROR")
                        emitError(it)
                    }
                )
        )
    }

    private fun makeListItems(
        observeRatiosUseCase: IObserveRatiosUseCase,
        observeTrackedCodesUseCase: IObserveTrackedCodesUseCase
    ) = Observable.combineLatest(
        // CODES RATIOS CHANGED
        Observable.combineLatest(
            observeRatiosUseCase()
                .subscribeOn(Schedulers.io())
                .doOnNext { lastUpdatedLiveData.postValue(DataUpdatedTime(it.time)) },
            // TRACKING CODES CHANGED
            observeTrackedCodesUseCase()
                .subscribeOn(Schedulers.io())
                .doOnNext { codeWithData -> handleEmptyState(codeWithData) }
                .observeOn(Schedulers.computation())
        ) { allRatios, trackedCodes ->
            mapCodesToData(codes = trackedCodes, allRatios = allRatios.ratios)
        }
            .map { codeWithData -> Pair(System.nanoTime(), codeWithData) },
        // USER INPUT NEW BASE CURRENCY
        baseAmountChange
            .subscribeOn(Schedulers.computation())
            .observeOn(Schedulers.computation())
            .map { newAmount -> Pair(System.nanoTime(), newAmount) }
    ) { currenciesChange, newBaseAmount ->
        val trackedCodesWithData = currenciesChange.second

        if (trackedCodesWithData.isEmpty()) {
            listOf()
        } else {
            val orderChangeTime = currenciesChange.first
            val baseAmountChangeTime = newBaseAmount.first
            // ratios or tracking order changed
            if (orderChangeTime > baseAmountChangeTime) {
                makeListItemModels(
                    baseAmount = currenciesListModels.find { it.code == trackedCodesWithData.first().code }
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

    private fun handleEmptyState(codeWithData: List<SupportedCode>?) {
        if (codeWithData.isNullOrEmpty()) {
            showEmptyStateLiveData.postValue(true)
        } else {
            showEmptyStateLiveData.postValue(false)
        }
    }

    private fun fetchRatiosFromNetwork() {
        if (isLoadingLiveData.value != true) {
            addSubscription(
                fetchRemoteRatios()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { isLoadingLiveData.value = true }
                    .doOnTerminate { isLoadingLiveData.value = false }
                    .subscribeBy(
                        onError = {
                            eventsLogger.logNonFatalError(it, "REMOTE RATIOS REQUEST FAILED")
                            emitError(it)
                        }
                    )
            )
        }
    }

    fun baseCurrencyAmountChanged(newAmount: Double) {
        if (newAmount != baseAmountChange.value) {
            baseAmountChange.onNext(newAmount)
        }
    }

    fun listItemClicked(newBaseCurrency: SupportedCode) {
        // ignore base item clicks
        if (currenciesListModels.isNotEmpty() && currenciesListModels.first().code != newBaseCurrency) {
            moveTrackedCodeToTop(newBaseCurrency)
                .subscribeOn(Schedulers.io())
                .subscribe()
        }
    }

    fun itemDragged(from: Int, to: Int) {
        swapTrackedCodes(
            currenciesListModels[from].code,
            currenciesListModels[to].code
        )
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun refreshButtonClicked() {
        eventsLogger.logEvent(ButtonClickedEvent("refresh_button"))
        fetchRatiosFromNetwork()
    }

    fun infoButtonClicked() {
        eventsLogger.logEvent(ButtonClickedEvent("info_button"))
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

    // TODO STRINGS!
    private fun emitError(t: Throwable?) {
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

    private fun assignAndEmitModels(listItemsModels: List<HomeListItemModel>) {
        currenciesListModels = listItemsModels
        currenciesListModelsLiveData.postValue(Resource.Success(listItemsModels))
    }

    @WorkerThread
    private fun makeListItemModels(
        baseAmount: Double,
        trackedCodesWithData: List<CodeWithData>
    ): List<HomeListItemModel> {
        val baseCode = trackedCodesWithData.first().code
        val baseToUahRatio = trackedCodesWithData.first().toUahRatio

        return trackedCodesWithData.mapIndexed { index, trackedCodeWithData ->
            val newCurrencyCode = trackedCodeWithData.code
            val newCurrencyRatio = trackedCodeWithData.toUahRatio
            val newCurrencyData = trackedCodeWithData.staticData

            if (index == 0) {
                HomeListItemModel.Base(
                    code = baseCode,
                    codeStaticData = newCurrencyData,
                    amount = baseAmount
                )
            } else {
                HomeListItemModel.NonBase(
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

    // Exposing only LiveData
    fun getCurrenciesLiveData(): LiveData<Resource<List<HomeListItemModel>>> =
        currenciesListModelsLiveData

    fun getShowEmptyStateLiveData(): LiveData<Boolean> =
        showEmptyStateLiveData

    fun getLastUpdatedLiveData(): LiveData<DataUpdatedTime> =
        lastUpdatedLiveData

    fun getIsLoadingLiveData(): LiveData<Boolean> =
        isLoadingLiveData

    private companion object {
        const val DEFAULT_BASE_AMOUNT = 100.0
    }
}
