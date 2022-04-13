package com.mieszko.currencyconverter.common.model

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

/**
 * Disposables bag - an abstraction of CompositeDisposable
 * The purpose if this class is to make compositeDisposableTestable
 *
 * @constructor Create empty Disposables bag
 */
interface IDisposablesBag {
    fun add(disposable: Disposable): Boolean
    fun clear()
}

class DisposablesBag(private val compositeDisposable: CompositeDisposable) : IDisposablesBag {
    override fun clear() = compositeDisposable.clear()
    override fun add(disposable: Disposable) = compositeDisposable.add(disposable)
}
