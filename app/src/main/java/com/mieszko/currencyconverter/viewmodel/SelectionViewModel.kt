package com.mieszko.currencyconverter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mieszko.currencyconverter.common.SupportedCode
import com.mieszko.currencyconverter.data.model.AllCurrenciesListModel
import com.mieszko.currencyconverter.data.model.TrackedCurrenciesListModel
import com.mieszko.currencyconverter.data.repository.ICodesDataRepository
import com.mieszko.currencyconverter.data.repository.ITrackedCurrenciesRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

// TODO USE USECASES INSTEAD
class SelectionViewModel(
    private val trackedCurrenciesRepository: ITrackedCurrenciesRepository,
    private val codesDataRepository: ICodesDataRepository
) :
    ViewModel() {
    private val disposeBag = CompositeDisposable()

    private val trackedCurrenciesLiveData: MutableLiveData<Pair<List<TrackedCurrenciesListModel>, List<AllCurrenciesListModel>>> =
        MutableLiveData()

    //Exposing only LiveData
    fun getTrackedCurrenciesLiveData(): LiveData<Pair<List<TrackedCurrenciesListModel>, List<AllCurrenciesListModel>>> =
        trackedCurrenciesLiveData

    //TODO GO FOR ENUM MAP AGAIN
    //TODO GET THREADING RIGHT
    init {
        disposeBag.add(
            getTrackedCurrencies()
                .subscribeOn(Schedulers.io())
                .flatMapSingle { trackedCodes ->
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

    //todo usecase
    private fun getTrackedCurrencies(): Observable<List<SupportedCode>> {
        return trackedCurrenciesRepository
            .getTrackedCurrencies()
    }

    private fun emitData(data: Pair<List<TrackedCurrenciesListModel>, List<AllCurrenciesListModel>>) {
        trackedCurrenciesLiveData.postValue(data)
    }

    //todo usecase
    fun trackedCurrenciesItemClicked(currency: TrackedCurrenciesListModel) {
        trackedCurrenciesRepository
            .removeTrackedCurrency(currency.code)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun allCurrenciesItemClicked(allCurrenciesListModel: AllCurrenciesListModel) {
        if (allCurrenciesListModel.isTracked) {
            //todo usecase
            trackedCurrenciesRepository
                .removeTrackedCurrency(allCurrenciesListModel.code)
                .subscribeOn(Schedulers.io())
                .subscribe()
        } else {
            //todo usecase
            trackedCurrenciesRepository
                .addTrackedCurrency(allCurrenciesListModel.code)
                .subscribeOn(Schedulers.io())
                .subscribe()
        }
    }
}