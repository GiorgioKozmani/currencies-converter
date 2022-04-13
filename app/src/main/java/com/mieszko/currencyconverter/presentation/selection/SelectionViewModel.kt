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
import com.mieszko.currencyconverter.domain.repository.ITrackedCodesRepository
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.ICreateTrackedCodesModelsUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud.IAddTrackedCodesUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud.IRemoveTrackedCodesUseCase
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import java.util.concurrent.TimeUnit

class SelectionViewModel(
    disposablesBag: IDisposablesBag,
    trackedCodesRepository: ITrackedCodesRepository,
    private val removeTrackedCodes: IRemoveTrackedCodesUseCase,
    private val addTrackedCodes: IAddTrackedCodesUseCase,
    private val createTrackedCodesModels: ICreateTrackedCodesModelsUseCase,
    private val eventsLogger: IFirebaseEventsLogger
) : BaseViewModel(disposablesBag) {

    private val trackedCurrenciesLiveData: MutableLiveData<List<TrackingCurrenciesModel>> =
        MutableLiveData()

    private val searchQueryChange: BehaviorSubject<String> = BehaviorSubject.createDefault("")

    init {
        setupListDataSource(trackedCodesRepository.observeTrackedCodes())
        setupSearchQueryLogging()
    }

    private fun setupListDataSource(
        observeTrackedCodes: Observable<List<SupportedCode>>
    ) {
        addSubscription(
            Observable.combineLatest(
                observeTrackedCodes
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .flatMapSingle { trackedCodes ->
                        createTrackedCodesModels(trackedCodes)
                            .subscribeOn(Schedulers.computation())
                    },
                searchQueryChange
                    .subscribeOn(Schedulers.computation())
                    .observeOn(Schedulers.computation())
            ) { sortedItems, searchQuery ->
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
                .subscribeOn(Schedulers.computation())
                .subscribeBy(
                    onNext = { data -> emitData(data) },
                    onError = {
                        // todo handle
                    }
                )
        )
    }

    private fun setupSearchQueryLogging() {
        addSubscription(
            searchQueryChange
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .debounce(500, TimeUnit.MILLISECONDS)
                .filter { it.isNotBlank() }
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

    private fun emitData(data: List<TrackingCurrenciesModel>) {
        trackedCurrenciesLiveData.postValue(data)
    }

    fun itemClicked(trackingCurrenciesModel: TrackingCurrenciesModel) {
        if (trackingCurrenciesModel.isTracked) {
            removeTrackedCodes(trackingCurrenciesModel.code)
                .subscribeOn(Schedulers.io())
                .doOnComplete {
                    eventsLogger.logEvent(
                        CodeUntrackedEvent(trackingCurrenciesModel.code, searchQueryChange.value)
                    )
                }
                .subscribe()
        } else {
            addTrackedCodes(trackingCurrenciesModel.code)
                .subscribeOn(Schedulers.io())
                .doOnComplete {
                    eventsLogger.logEvent(
                        CodeTrackedEvent(trackingCurrenciesModel.code, searchQueryChange.value)
                    )
                }
                .subscribe()
        }
    }

    fun searchQueryChanged(newQuery: String) {
        searchQueryChange.onNext(newQuery)
    }

    // Exposing only LiveData
    fun getTrackedCurrenciesLiveData(): LiveData<List<TrackingCurrenciesModel>> =
        trackedCurrenciesLiveData
}
