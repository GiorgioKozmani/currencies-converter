package com.mieszko.currencyconverter.presentation.tracking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mieszko.currencyconverter.common.base.BaseViewModel
import com.mieszko.currencyconverter.common.util.IDisposablesBag
import com.mieszko.currencyconverter.common.model.SupportedCode
import com.mieszko.currencyconverter.domain.model.list.TrackingCurrenciesModel
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

    private val trackedCurrenciesLiveData: MutableLiveData<List<TrackingCurrenciesModel>> =
        MutableLiveData()

    //Exposing only LiveData
    fun getTrackedCurrenciesLiveData(): LiveData<List<TrackingCurrenciesModel>> =
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
                        getAllCurrenciesModels(trackedCodes)
                            .subscribeOn(Schedulers.computation())
                    },
                searchQueryChange
                    .subscribeOn(Schedulers.computation())
                    .observeOn(Schedulers.computation()),
                { allItems, searchQuery ->
                    if (searchQuery.isNotEmpty()) {
                        //TODO FOR SEARCH TO WORK I NEED TO STRINGS ALREADY, NOT RESOURCES
                        //todo allow also search by country
                        allItems.filter {
                            it.code.name.contains(
                                searchQuery,
                                true
                            ) || it.codeData.name.contains(searchQuery, true)
                        }
                    } else {
                        val trackedItems = allItems
                            .filter { it.isTracked }
                            .sortedBy { it.codeData.name }

                        val notTrackedItems = allItems
                            .subtract(trackedItems)
                            .sortedBy { it.codeData.name }

                        mutableListOf<TrackingCurrenciesModel>().apply {
                            addAll(trackedItems)
                            addAll(notTrackedItems)
                        }
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



    // TODO NEW MODEL FOR THIS CASE CONTAINTING COUNTRY (COUNTRIES?) SO WE CAN QUERY AGAINST THEM
    private fun getAllCurrenciesModels(trackedCurrencies: List<SupportedCode>): Single<List<TrackingCurrenciesModel>> =
        //todo check if there's any difference between this and Single.just()
        Single.fromCallable {
            SupportedCode
                .values()
                .mapNotNull { code ->
                    codesDataRepository.getCodeStaticData(code)?.let { staticData ->
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

    fun allCurrenciesItemClicked(trackingCurrenciesModel: TrackingCurrenciesModel) {
        if (trackingCurrenciesModel.isTracked) {
            //todo usecase
            removeTrackedCodesUseCase(trackingCurrenciesModel.code)
                .subscribeOn(Schedulers.io())
                .subscribe()
        } else {
            //todo usecase
            addTrackedCodesUseCase(trackingCurrenciesModel.code)
                .subscribeOn(Schedulers.io())
                .subscribe()
        }
    }

    fun searchQueryChanged(newQuery: String) {
        searchQueryChange.onNext(newQuery)
    }
}