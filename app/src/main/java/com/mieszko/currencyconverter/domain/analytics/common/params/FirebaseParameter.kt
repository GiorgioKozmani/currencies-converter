package com.mieszko.currencyconverter.domain.analytics.common.params

import android.os.Bundle

sealed class FirebaseParameter {
    abstract val key: String

    abstract fun addToBundle(bundle: Bundle): Bundle

    data class StringParameter(override val key: String, val value: String) : FirebaseParameter() {
        override fun addToBundle(bundle: Bundle): Bundle = bundle.apply { putString(key, value) }
    }
}
