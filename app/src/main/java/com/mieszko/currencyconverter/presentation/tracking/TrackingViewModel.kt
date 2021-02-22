package com.mieszko.currencyconverter.presentation.tracking

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mieszko.currencyconverter.common.BaseViewModel
import com.mieszko.currencyconverter.common.IDisposablesBag
import com.mieszko.currencyconverter.common.SupportedCode
import com.mieszko.currencyconverter.domain.model.AllCurrenciesListModel
import com.mieszko.currencyconverter.domain.model.TrackedCurrenciesListModel
import com.mieszko.currencyconverter.domain.repository.ICodesDataRepository
import com.mieszko.currencyconverter.domain.repository.ITrackedCurrenciesRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

// TODO USE USECASES INSTEAD
class TrackingViewModel(
    disposablesBag: IDisposablesBag,
    private val trackedCurrenciesRepository: ITrackedCurrenciesRepository,
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