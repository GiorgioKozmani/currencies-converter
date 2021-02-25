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
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

class TrackingViewModel(
    disposablesBag: IDisposablesBag,
    private val removeTrackedCodesUseCase: IRemoveTrackedCodesUseCase,
    observeTrackedCodesUseCase: IObserveTrackedCodesUseCase,
    private val addTrackedCodesUseCase: IAddTrackedCodesUseCase,
    // TODO USE USECASES INSTEAD
    private val codesDataRepository: ICodesDataRepository
) : BaseViewModel(disposablesBag) {

    private val trackedCurrenciesLiveData: MutableLiveData<Pair<List<TrackedCurrenciesListModel>, List<AllCurrenciesListModel>>> =
        MutableLiveData()

    //Exposing only LiveData
    fun getTrackedCurrenciesLiveData(): LiveData<Pair<List<TrackedCurrenciesListModel>, List<AllCurrenciesListModel>>> =
        trackedCurrenciesLiveData

    //TODO GO FOR ENUM MAP AGAIN
    //TODO GET THREADING RIGHT
    init {
        disposablesBag.add(
            observeTrackedCodesUseCase()
                .subscribeOn(Schedulers.io())
                .flatMapSingle { trackedCodes ->
                    //todo this is a good example for usecase
                    Single.zip(
                        getTrackedCurrenciesModels(trackedCodes)
                            .subscribeOn(Schedulers.computation()),
                        getAllCurrenciesModels(trackedCodes)
                            .subscribeOn(Schedulers.computation()),
                        { trackedModels, allModels -> Pair(trackedModels, allModels) }
                    )
                }
                .subscribeOn(Schedulers.computation())
                .subscribe(::emitData)
        )
    }

    //todo this should be handled by separate usecase
    private fun getTrackedCurrenciesModels(trackedCodes: List<SupportedCode>) =
        Single.fromCallable {
            trackedCodes.map { trackedCode ->
                TrackedCurrenciesListModel(
                    trackedCode,
                    codesDataRepository.getCodeData(trackedCode)
                )
            }
        }


    private fun getAllCurrenciesModels(trackedCurrencies: List<SupportedCode>): Single<List<AllCurrenciesListModel>> =
        //todo check if there's any difference between this and Single.just()
        Single.fromCallable {
            SupportedCode
                .values()
                .map { code ->
                    AllCurrenciesListModel(
                        code,
                        codesDataRepository.getCodeData(code),
                        trackedCurrencies.contains(code)
                    )
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
}