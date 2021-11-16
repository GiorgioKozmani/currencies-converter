package com.mieszko.currencyconverter.presentation.selection

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mieszko.currencyconverter.common.base.BaseViewModel
import com.mieszko.currencyconverter.common.model.IDisposablesBag
import com.mieszko.currencyconverter.common.model.SupportedCode
import com.mieszko.currencyconverter.domain.analytics.IFirebaseEventsLogger
import com.mieszko.currencyconverter.domain.analytics.events.CodeTrackedEvent
import com.mieszko.currencyconverter.domain.analytics.events.CodeUntrackedEvent
import com.mieszko.currencyconverter.domain.analytics.events.SearchTermEvent
import com.mieszko.currencyconverter.domain.model.list.TrackingCurrenciesModel
import com.mieszko.currencyconverter.domain.repository.ICodesStaticDataRepository
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud.IAddTrackedCodesUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud.IObserveTrackedCodesUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud.IRemoveTrackedCodesUseCase
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class SelectionViewModel(
    disposablesBag: IDisposablesBag,
    observeTrackedCodesUseCase: IObserveTrackedCodesUseCase,
    private val removeTrackedCodesUseCase: IRemoveTrackedCodesUseCase,
    private val addTrackedCodesUseCase: IAddTrackedCodesUseCase,
    // TODO USE USECASES INSTEAD
    private val codesStaticDataRepository: ICodesStaticDataRepository,
    private val eventsLogger: IFirebaseEventsLogger
) : BaseViewModel(disposablesBag) {

    private val trackedCurrenciesLiveData: MutableLiveData<List<TrackingCurrenciesModel>> =
        MutableLiveData()

    // Exposing only LiveData
    fun getTrackedCurrenciesLiveData(): LiveData<List<TrackingCurrenciesModel>> =
        trackedCurrenciesLiveData

    private val searchQueryChange: BehaviorSubject<String> = BehaviorSubject.createDefault("")

    init {
        setupListDataSource(disposablesBag, observeTrackedCodesUseCase)
        setupSearchQueryLogging(disposablesBag)
    }

    private fun setupListDataSource(
        disposablesBag: IDisposablesBag,
        observeTrackedCodesUseCase: IObserveTrackedCodesUseCase
    ) {
        // TODO GO FOR ENUM MAP AGAIN
        // TODO GET THREADING RIGHT
        disposablesBag.add(
            Observable.combineLatest(
                observeTrackedCodesUseCase()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .flatMapSingle { trackedCodes ->
                        getCurrenciesModels(trackedCodes)
                            .subscribeOn(Schedulers.computation())
                    },
                searchQueryChange
                    .subscribeOn(Schedulers.computation())
                    .observeOn(Schedulers.computation()),
                { sortedItems, searchQuery ->
                    if (searchQuery.isNotEmpty()) {
                        sortedItems.filter {
                            it.code.name.contains(
                                searchQuery,
                                true
                            ) || it.codeStaticData.name.contains(searchQuery, true)
                        }
                    } else {
                        sortedItems
                    }
                }
            )
                .subscribeOn(Schedulers.computation())
                .subscribeBy(
                    onNext = { data -> emitData(data) },
                    onError = {
                        // todo handle
                    }
                )
        )
    }

    private fun setupSearchQueryLogging(disposablesBag: IDisposablesBag) {
        disposablesBag.add(
            searchQueryChange
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .debounce(500, TimeUnit.MILLISECONDS)
                .filter { it.isNotBlank() }
                .doOnNext { }
                .subscribeBy(
                    onNext = { searchTerm ->
                        eventsLogger.logEvent(SearchTermEvent(searchTerm))
                    },
                    onError = {
                        // ignore
                    }
                )
        )
    }

    // TODO ONLY GET CURRENCIES THAT WE HAVE RATIOS AND DATA FOR? (NEW DEFINITION OF SUPPORTED CURRENCY)
    // TODO NEW MODEL FOR THIS CASE CONTAINTING COUNTRY (COUNTRIES?) SO WE CAN QUERY AGAINST THEM
    private fun getCurrenciesModels(trackedCurrencies: List<SupportedCode>): Single<List<TrackingCurrenciesModel>> =
        // todo check if there's any difference between this and Single.just()
        Single.fromCallable {
            val allCurrencies = SupportedCode.values()

            val notTrackedCodes = allCurrencies
                .subtract(trackedCurrencies)

            // this way we persist the order of tracked currencies
            mutableListOf<SupportedCode>().apply {
                addAll(trackedCurrencies)
                addAll(notTrackedCodes)
            }
                .mapNotNull { code ->
                    codesStaticDataRepository.getCodeStaticData(code)?.let { staticData ->
                        TrackingCurrenciesModel(
                            code,
                            staticData,
                            trackedCurrencies.contains(code)
                        )
                    }
                }
        }

    private fun emitData(data: List<TrackingCurrenciesModel>) {
        trackedCurrenciesLiveData.postValue(data)
    }

    fun itemClicked(trackingCurrenciesModel: TrackingCurrenciesModel) {
        if (trackingCurrenciesModel.isTracked) {
            removeTrackedCodesUseCase(trackingCurrenciesModel.code)
                .subscribeOn(Schedulers.io())
                .doOnComplete {
                    eventsLogger.logEvent(
                        CodeUntrackedEvent(
                            trackingCurrenciesModel.code,
                            searchQueryChange.value
                        )
                    )
                }
                .subscribe()
        } else {
            addTrackedCodesUseCase(trackingCurrenciesModel.code)
                .subscribeOn(Schedulers.io())
                .doOnComplete {
                    eventsLogger.logEvent(
                        CodeTrackedEvent(
                            trackingCurrenciesModel.code,
                            searchQueryChange.value
                        )
                    )
                }
                .subscribe()
        }
    }

    fun searchQueryChanged(newQuery: String) {
        searchQueryChange.onNext(newQuery)
    }
}
