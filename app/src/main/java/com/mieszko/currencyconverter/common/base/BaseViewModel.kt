package com.mieszko.currencyconverter.common.base

import androidx.lifecycle.ViewModel
import com.mieszko.currencyconverter.common.util.IDisposablesBag

abstract class BaseViewModel(val disposablesBag: IDisposablesBag) : ViewModel() {

    override fun onCleared() {
        disposablesBag.dispose()
        super.onCleared()
    }
}