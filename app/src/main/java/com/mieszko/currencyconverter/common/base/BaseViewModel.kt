package com.mieszko.currencyconverter.common.base

import androidx.lifecycle.ViewModel
import com.mieszko.currencyconverter.common.model.IDisposablesBag
import io.reactivex.rxjava3.disposables.Disposable

abstract class BaseViewModel(val disposablesBag: IDisposablesBag) : ViewModel() {

    override fun onCleared() {
        disposablesBag.dispose()
        super.onCleared()
    }

    fun addSubscription(disposable: Disposable) {
        disposablesBag.add(disposable)
    }
}
