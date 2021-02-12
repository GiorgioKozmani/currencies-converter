package com.mieszko.currencyconverter.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.mieszko.currencyconverter.common.SupportedCurrency
import com.mieszko.currencyconverter.data.model.SelectedCurrency
import com.mieszko.currencyconverter.data.persistance.SharedPrefs
import java.lang.reflect.Type

class SelectionViewModel : ViewModel() {

    val type: Type = object : TypeToken<List<SelectedCurrency>>() {}.type
    val gson = GsonBuilder().create()

    private val trackingCurrenciesLiveData: MutableLiveData<Pair<List<SelectedCurrency>, List<SelectedCurrency>>> =
        MutableLiveData()


    //Exposing only LiveData
    fun getTrackedCurrenciesLiveData(): LiveData<Pair<List<SelectedCurrency>, List<SelectedCurrency>>> =
        trackingCurrenciesLiveData


    //todo rethink mutablity
    private var allCurrenciesList = mutableListOf<SelectedCurrency>()

    //todo think of renaming all into tracked
    //todo 2 only names as string
    //todo have supported currencies as hashmap of short name (like enum now) and associated data (full, flagImg)
    private var trackedCurrenciesList = mutableListOf<SelectedCurrency>()

    private fun emitData() {
        trackingCurrenciesLiveData.postValue(Pair(trackedCurrenciesList.toList(), allCurrenciesList.toList()))
    }

    init {
        getTrackedCurrencies()
        getAllCurrencies()
        //todo do we have to on start?
        emitData()
    }

    private fun getTrackedCurrencies() {
        val savedJson = SharedPrefs.getString(SharedPrefs.Key.TrackedCurrencies)
        trackedCurrenciesList = gson.fromJson(savedJson, type) ?: mutableListOf()

        //todo do we have to on start?
    }

    private fun getAllCurrencies() {
        //todo from
        allCurrenciesList = SupportedCurrency
            .values()
            .mapTo(allCurrenciesList) { currency ->
                //todo this is ugly
                SelectedCurrency(currency, trackedCurrenciesList.find { it.currency == currency } != null)
            }
    }

    //todo this can turn into supported currency, both methods can
    fun trackedCurrenciesItemClicked(currency: SelectedCurrency) {
        trackedCurrenciesList.remove(currency)
        SharedPrefs.put(SharedPrefs.Key.TrackedCurrencies, gson.toJson(trackedCurrenciesList, type))

        //todo this is very inefficient. Find a way to use short as ID, investigate having supported currencies as hashmap [SHORT, DATA]
        allCurrenciesList[allCurrenciesList.indexOfFirst { it.currency == currency.currency }] = SelectedCurrency(currency.currency, false)

        emitData()
    }

    fun allCurrenciesItemClicked(selectedCurrency: SelectedCurrency) {
        val flippedTrackedFlag = !selectedCurrency.isTracked

        allCurrenciesList[allCurrenciesList.indexOf(selectedCurrency)] =
            selectedCurrency.copy(isTracked = flippedTrackedFlag)

        if (flippedTrackedFlag) {
            //todo this is ULTRA BAD
            trackedCurrenciesList.add(SelectedCurrency(selectedCurrency.currency, true))
        } else {
            trackedCurrenciesList.remove(selectedCurrency)
        }

        SharedPrefs.put(SharedPrefs.Key.TrackedCurrencies, gson.toJson(trackedCurrenciesList, type))

        emitData()
    }
}