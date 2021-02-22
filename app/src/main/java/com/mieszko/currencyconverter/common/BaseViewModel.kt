package com.mieszko.currencyconverter.common

import androidx.lifecycle.ViewModel

abstract class BaseViewModel(val disposablesBag: IDisposablesBag) : ViewModel() {

    override fun onCleared() {
        disposablesBag.dispose()
        super.onCleared()
    }
}