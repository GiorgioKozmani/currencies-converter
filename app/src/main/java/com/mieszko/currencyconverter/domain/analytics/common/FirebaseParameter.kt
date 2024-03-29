package com.mieszko.currencyconverter.domain.analytics.common

import android.os.Bundle

sealed class FirebaseParameter {
    abstract val key: String

    abstract fun addToBundle(bundle: Bundle): Bundle

    data class StringParameter(override val key: String, val value: String) : FirebaseParameter() {
        override fun addToBundle(bundle: Bundle): Bundle = bundle.apply { putString(key, value) }
    }

    data class DoubleParameter(override val key: String, val value: Double) : FirebaseParameter() {
        override fun addToBundle(bundle: Bundle): Bundle = bundle.apply { putDouble(key, value) }
    }

    data class IntParameter(override val key: String, val value: Int) : FirebaseParameter() {
        override fun addToBundle(bundle: Bundle): Bundle = bundle.apply { putInt(key, value) }
    }
}
