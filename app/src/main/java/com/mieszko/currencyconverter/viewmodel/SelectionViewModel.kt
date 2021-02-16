package com.mieszko.currencyconverter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mieszko.currencyconverter.common.SupportedCurrency
import com.mieszko.currencyconverter.data.model.SelectedCurrency
import com.mieszko.currencyconverter.data.repository.ITrackedCurrenciesRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

class SelectionViewModel(private val trackedCurrenciesRepository: ITrackedCurrenciesRepository) :
    ViewModel() {

    private val trackedCurrenciesLiveData: MutableLiveData<Pair<List<SupportedCurrency>, List<SelectedCurrency>>> =
        MutableLiveData()

    //Exposing only LiveData
    fun getTrackedCurrenciesLiveData(): LiveData<Pair<List<SupportedCurrency>, List<SelectedCurrency>>> =
        trackedCurrenciesLiveData

//    private var allCurrenciesList = mutableListOf<SelectedCurrency>()

    //todo 2 only names as string
    //todo have supported currencies as hashmap of short name (like enum now) and associated data (full, flagImg)
//    private var trackedCurrenciesList = mutableListOf<SupportedCurrency>()

    init {
        //todo disposable and stop observing on detach
        getTrackedCurrencies()
            .subscribeOn(Schedulers.io())
            .map { Pair(it, getAllCurrencies(it)) }
            .subscribe(::emitData)
    }

    private fun getTrackedCurrencies(): Observable<List<SupportedCurrency>> {
        return trackedCurrenciesRepository
            .getTrackedCurrencies()
            .subscribeOn(Schedulers.io())
    }

    private fun getAllCurrencies(trackedCurrencies: List<SupportedCurrency>): List<SelectedCurrency> {
        return SupportedCurrency
            .values()
            .map { currency ->
                SelectedCurrency(currency, trackedCurrencies.contains(currency))
            }
    }

    private fun emitData(data: Pair<List<SupportedCurrency>, List<SelectedCurrency>>) {
        trackedCurrenciesLiveData.postValue(data)
    }

    // todo to repo
    fun trackedCurrenciesItemClicked(currency: SupportedCurrency) {
        //todo just store names :>
//        trackedCurrenciesList.remove(currency)
//
//        SharedPrefsManager.put(
//            SharedPrefsManager.Key.TrackedCurrencies,
//            gson.toJson(trackedCurrenciesList, supportedCurrencyType)
//        )
//
//        //todo this is very inefficient. Find a way to use short as ID, investigate having supported currencies as hashmap [SHORT, DATA]
//        allCurrenciesList.indexOfFirst { it.currency == currency }.let { index ->
//            if (index != -1) {
//                allCurrenciesList[index] = allCurrenciesList[index].copy(isTracked = false)
//            }
//        }
//
//        emitData()
        trackedCurrenciesRepository
            .removeTrackedCurrency(currency)
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    fun allCurrenciesItemClicked(selectedCurrency: SelectedCurrency) {
//        val newTrackingFlag = !selectedCurrency.isTracked

        if (selectedCurrency.isTracked) {
            trackedCurrenciesRepository
                .removeTrackedCurrency(selectedCurrency.currency)
                .subscribeOn(Schedulers.io())
                .subscribe()
        } else {
            trackedCurrenciesRepository
                .addTrackedCurrency(selectedCurrency.currency)
                .subscribeOn(Schedulers.io())
                .subscribe()
        }

//        allCurrenciesList[allCurrenciesList.indexOf(selectedCurrency)] =
//            selectedCurrency.copy(isTracked = newTrackingFlag)
//
//        if (newTrackingFlag) {
//            trackedCurrenciesList.add(selectedCurrency.currency)
//        } else {
//            trackedCurrenciesList.remove(selectedCurrency.currency)
//        }
//
//        SharedPrefsManager.put(
//            SharedPrefsManager.Key.TrackedCurrencies,
//            gson.toJson(trackedCurrenciesList, supportedCurrencyType)
//        )
//
//        emitData()
    }
}