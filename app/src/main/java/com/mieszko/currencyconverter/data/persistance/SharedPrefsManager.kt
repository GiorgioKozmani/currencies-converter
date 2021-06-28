package com.mieszko.currencyconverter.data.persistance

import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import java.util.Date

interface ISharedPrefsManager {
    // KEYS
    enum class Key(val value: String) {
        CachedRatios("CACHED_RATIOS"),
        TrackedCurrencies("TRACKED_CURRENCIES"),
        FirstAppOpenDate("FIRST_APP_OPEN"),
        InAppRate("IN_APP_RATE")
    }

    fun put(key: Key, toPut: String)
    fun put(key: Key, toPut: Date)
    fun put(key: Key, toPut: Int)

    fun getString(key: Key, defValue: String? = null): String?
    fun getDate(key: Key): Date?
    fun getInt(key: Key): Int
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

    override fun getString(key: ISharedPrefsManager.Key, defValue: String?): String? {
        return mSharedPrefs.getString(key.value, defValue)
    }

    // DATE
    /**
     * Stores [Date.getTime] that can be later converted back into date using [getDate]
     */
    override fun put(key: ISharedPrefsManager.Key, toPut: Date) {
        editor.putLong(key.value, toPut.time).apply()
    }

    /**
     * Returns null if key value cannot be converted to [Date]
     */
    override fun getDate(key: ISharedPrefsManager.Key): Date? {
        return try {
            val dateLong = mSharedPrefs.getLong(key.value, -1L)
            if (dateLong != -1L) {
                Date(dateLong)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    // INTEGER
    override fun put(key: ISharedPrefsManager.Key, toPut: Int) {
        editor.putInt(key.value, toPut).apply()
    }

    override fun getInt(key: ISharedPrefsManager.Key): Int {
        return mSharedPrefs.getInt(key.value, -1)
    }
}
