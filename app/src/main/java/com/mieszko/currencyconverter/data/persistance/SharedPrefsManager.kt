package com.mieszko.currencyconverter.data.persistance

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager

interface ISharedPrefsManager {
    // STRING
    fun put(key: Key, toPut: String)
    fun getString(key: Key): String?
    fun getString(key: Key, defValue: String): String?

    // KEYS
    enum class Key(val value: String) {
        CachedRatios("CACHED_RATIOS"),
        TrackedCurrencies("TRACKED_CURRENCIES")
    }
}

class SharedPrefsManager(context: Application) : ISharedPrefsManager {
    private val mSharedPrefs: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    private val editor: SharedPreferences.Editor
        get() = mSharedPrefs.edit()

    // STRING
    override fun put(key: ISharedPrefsManager.Key, toPut: String) {
        editor.putString(key.value, toPut).apply()
    }

    override fun getString(key: ISharedPrefsManager.Key): String? {
        return mSharedPrefs.getString(key.value, null)
    }

    override fun getString(key: ISharedPrefsManager.Key, defValue: String): String? {
        return mSharedPrefs.getString(key.value, defValue)
    }
}
