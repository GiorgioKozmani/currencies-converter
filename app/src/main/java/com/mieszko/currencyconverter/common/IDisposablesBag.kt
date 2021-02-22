package com.mieszko.currencyconverter.common

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.disposables.DisposableContainer

/**
 * Disposables bag - an abstraction of CompositeDisposable
 * The purpose if this class is to make compositeDisposableTestable
 *
 * @constructor Create empty Disposables bag
 */
interface IDisposablesBag : Disposable, DisposableContainer {
    fun clear()
    fun size(): Int
}

class DisposablesBag(private val compositeDisposable: CompositeDisposable) : IDisposablesBag {
    override fun clear() = compositeDisposable.clear()

    override fun dispose() = compositeDisposable.dispose()

    override fun add(disposable: Disposable) = compositeDisposable.add(disposable)

    override fun remove(disposable: Disposable) = compositeDisposable.remove(disposable)

    override fun delete(disposable: Disposable) = compositeDisposable.delete(disposable)

    override fun size(): Int = compositeDisposable.size()

    override fun isDisposed(): Boolean = compositeDisposable.isDisposed
}