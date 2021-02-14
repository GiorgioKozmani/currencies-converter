package com.mieszko.currencyconverter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mieszko.currencyconverter.common.SupportedCurrency
import com.mieszko.currencyconverter.data.model.SelectedCurrency
import com.mieszko.currencyconverter.data.persistance.SharedPrefs
import java.lang.reflect.Type

class SelectionViewModel : ViewModel() {

    private val supportedCurrencyType: Type = object : TypeToken<List<SupportedCurrency>>() {}.type
    private val gson: Gson = GsonBuilder().create()

    private val trackingCurrenciesLiveData: MutableLiveData<Pair<List<SupportedCurrency>, List<SelectedCurrency>>> =
        MutableLiveData()


    //Exposing only LiveData
    fun getTrackedCurrenciesLiveData(): LiveData<Pair<List<SupportedCurrency>, List<SelectedCurrency>>> =
        trackingCurrenciesLiveData

    private var allCurrenciesList = mutableListOf<SelectedCurrency>()

    //todo 2 only names as string
    //todo have supported currencies as hashmap of short name (like enum now) and associated data (full, flagImg)
    private var trackedCurrenciesList = mutableListOf<SupportedCurrency>()

    init {
        trackedCurrenciesList = getTrackedCurrencies().toMutableList()
        getAllCurrencies(trackedCurrenciesList)

        emitData()
    }

    private fun emitData() {
        trackingCurrenciesLiveData.postValue(Pair(trackedCurrenciesList, allCurrenciesList))
    }

    //todo move to repo, background thread
    private fun getTrackedCurrencies(): List<SupportedCurrency> {
        val savedJson = SharedPrefs.getString(SharedPrefs.Key.TrackedCurrencies)
        return gson.fromJson(savedJson, supportedCurrencyType) ?: mutableListOf()
    }

    private fun getAllCurrencies(trackedCurrencies: List<SupportedCurrency>) {
        SupportedCurrency.values()
            .mapTo(allCurrenciesList) { currency ->
                SelectedCurrency(currency, trackedCurrencies.contains(currency))
            }
    }

    // todo to repo
    fun trackedCurrenciesItemClicked(currency: SupportedCurrency) {
        trackedCurrenciesList.remove(currency)
        SharedPrefs.put(
            SharedPrefs.Key.TrackedCurrencies,
            gson.toJson(trackedCurrenciesList, supportedCurrencyType)
        )

        //todo this is very inefficient. Find a way to use short as ID, investigate having supported currencies as hashmap [SHORT, DATA]
        allCurrenciesList.indexOfFirst { it.currency == currency }.let { index ->
            if (index != -1) {
                allCurrenciesList[index] = allCurrenciesList[index].copy(isTracked = false)
            }
        }

        emitData()
    }

    fun allCurrenciesItemClicked(selectedCurrency: SelectedCurrency) {
        val newTrackingFlag = !selectedCurrency.isTracked

        allCurrenciesList[allCurrenciesList.indexOf(selectedCurrency)] =
            selectedCurrency.copy(isTracked = newTrackingFlag)

        if (newTrackingFlag) {
            trackedCurrenciesList.add(selectedCurrency.currency)
        } else {
            trackedCurrenciesList.remove(selectedCurrency.currency)
        }

        SharedPrefs.put(
            SharedPrefs.Key.TrackedCurrencies,
            gson.toJson(trackedCurrenciesList, supportedCurrencyType)
        )

        emitData()
    }
}