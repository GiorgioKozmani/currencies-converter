package com.mieszko.currencyconverter.data.persistance

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

object SharedPrefs {
    private var mSharedPrefs: SharedPreferences? = null

    fun init(context: Context) {
        //todo androidX
        if (mSharedPrefs == null) mSharedPrefs =
            PreferenceManager.getDefaultSharedPreferences(context)
    }

    //COMMON
    fun containsKey(key: Key): Boolean {
        return mSharedPrefs!!.contains(key.value)
    }

    fun removeKey(key: Key) {
        editor.remove(key.value).apply()
    }

    //STRING
    fun put(key: Key, toPut: String?) {
        editor.putString(key.value, toPut).apply()
    }

    fun getString(key: Key): String? {
        return mSharedPrefs!!.getString(key.value, null)
    }

    fun getString(key: Key, defValue: String): String? {
        return mSharedPrefs!!.getString(key.value, defValue)
    }

    //LONG
    fun put(key: Key, toPut: Long) {
        editor.putLong(key.value, toPut).apply()
    }

    fun getLong(key: Key, defValue: Long): Long {
        return mSharedPrefs!!.getLong(key.value, defValue)
    }

    fun getLong(key: Key): Long {
        return mSharedPrefs!!.getLong(key.value, -1)
    }

    //KEYS
    enum class Key(val value: String) {
        // Remember Inbox configuration (position and tab)
        SavedCurrencies("SAVED_CURRENCIES"),
        SavedCurrenciesTime("SAVED_CURRENCIES_TIME"),
    }

    //PRIVATE
    private val editor: SharedPreferences.Editor
        get() = mSharedPrefs!!.edit()
}