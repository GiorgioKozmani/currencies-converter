package com.mieszko.currencyconverter.presentation.tracking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mieszko.currencyconverter.common.BaseViewModel
import com.mieszko.currencyconverter.common.IDisposablesBag
import com.mieszko.currencyconverter.common.SupportedCode
import com.mieszko.currencyconverter.domain.model.list.AllCurrenciesListModel
import com.mieszko.currencyconverter.domain.model.list.TrackedCurrenciesListModel
import com.mieszko.currencyconverter.domain.repository.ICodesDataRepository
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud.IAddTrackedCodesUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud.IObserveTrackedCodesUseCase
import com.mieszko.currencyconverter.domain.usecase.trackedcodes.crud.IRemoveTrackedCodesUseCase
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.Subject

//TODO STEP AWAY FROM TRACKED CURRENCIES AND INTRODUCE FILTER BUTTON INSTEAD!
class TrackingViewModel(
    disposablesBag: IDisposablesBag,
    observeTrackedCodesUseCase: IObserveTrackedCodesUseCase,
    private val removeTrackedCodesUseCase: IRemoveTrackedCodesUseCase,
    private val addTrackedCodesUseCase: IAddTrackedCodesUseCase,
    // TODO USE USECASES INSTEAD
    private val codesDataRepository: ICodesDataRepository
) : BaseViewModel(disposablesBag) {

    private val trackedCurrenciesLiveData: MutableLiveData<Pair<List<TrackedCurrenciesListModel>, List<AllCurrenciesListModel>>> =
        MutableLiveData()

    //Exposing only LiveData
    fun getTrackedCurrenciesLiveData(): LiveData<Pair<List<TrackedCurrenciesListModel>, List<AllCurrenciesListModel>>> =
        trackedCurrenciesLiveData

    private val searchQueryChange: Subject<String> = BehaviorSubject.createDefault("")

    //TODO GO FOR ENUM MAP AGAIN
    //TODO GET THREADING RIGHT
    init {
        disposablesBag.add(
            Observable.combineLatest(
                observeTrackedCodesUseCase()
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .flatMapSingle { trackedCodes ->
                        //todo this is a good example for usecase
                        Single.zip(
                            getTrackedCurrenciesModels(trackedCodes)
                                .subscribeOn(Schedulers.computation()),
                            getAllCurrenciesModels(trackedCodes)
                                .subscribeOn(Schedulers.computation()),
                            { trackedModels, allModels -> Pair(trackedModels, allModels) }
                        )
                    },
//                    .map { listItems -> Pair(System.nanoTime(), listItems) },
                searchQueryChange
                    .subscribeOn(Schedulers.computation())
                    .observeOn(Schedulers.computation()),
//                    .map { query -> Pair(System.nanoTime(), query.trim()) },
                { fullData, searchQuery ->
                    if (searchQuery.isNotEmpty()) {
                        Pair(
                            listOf(),
                            //TODO FOR SEARCH TO WORK I NEED TO STRINGS ALREADY, NOT RESOURCES
                            //todo allow also search by country
                            fullData.second.filter {
                                it.code.name.contains(
                                    searchQuery,
                                    true
                                ) || it.codeData.name.contains(searchQuery, true)
                            })
                    } else {
                        fullData
                    }
                })
                .subscribeOn(Schedulers.computation())
                .subscribeBy(
                    onNext = { data ->
                        emitData(data)
                    },
                    onError = {
                        //todo handle
                    }
                )
        )
    }

    //todo this should be handled by separate usecase
    private fun getTrackedCurrenciesModels(trackedCodes: List<SupportedCode>) =
        Single.fromCallable {
            trackedCodes.mapNotNull { trackedCode ->
                codesDataRepository.getCodeStaticData(trackedCode)?.let { staticData ->
                    TrackedCurrenciesListModel(
                        trackedCode,
                        staticData
                    )
                }
            }
        }


    // TODO NEW MODEL FOR THIS CASE CONTAINTING COUNTRY (COUNTRIES?) SO WE CAN QUERY AGAINST THEM
    private fun getAllCurrenciesModels(trackedCurrencies: List<SupportedCode>): Single<List<AllCurrenciesListModel>> =
        //todo check if there's any difference between this and Single.just()
        Single.fromCallable {
            SupportedCode
                .values()
                .mapNotNull { code ->
                    codesDataRepository.getCodeStaticData(code)?.let { staticData ->
                        AllCurrenciesListModel(
                            code,
                            staticData,
                            trackedCurrencies.contains(code)
                        )
                    }
                }
        }

    private fun emitData(data: Pair<List<TrackedCurrenciesListModel>, List<AllCurrenciesListModel>>) {
        trackedCurrenciesLiveData.postValue(data)
    }

    //todo usecase
    fun trackedCurrenciesItemClicked(currency: TrackedCurrenciesListModel) {
        removeTrackedCodesUseCase(currency.code)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun allCurrenciesItemClicked(allCurrenciesListModel: AllCurrenciesListModel) {
        if (allCurrenciesListModel.isTracked) {
            //todo usecase
            removeTrackedCodesUseCase(allCurrenciesListModel.code)
                .subscribeOn(Schedulers.io())
                .subscribe()
        } else {
            //todo usecase
            addTrackedCodesUseCase(allCurrenciesListModel.code)
                .subscribeOn(Schedulers.io())
                .subscribe()
        }
    }

    fun searchQueryChanged(newQuery: String) {
        searchQueryChange.onNext(newQuery)
    }
}