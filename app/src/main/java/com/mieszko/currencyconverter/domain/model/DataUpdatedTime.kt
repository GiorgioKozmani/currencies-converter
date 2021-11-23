package com.mieszko.currencyconverter.domain.model

import java.util.Date

data class DataUpdatedTime(private val updateDate: Date) {

    /**
     * Data is considered fresh if it was obtained within the last [FRESH_THRESHOLD_MS] milliseconds.
     */
    fun getDataState(): DataState {
        val currentTime = Date().time
        val updatedTime = updateDate.time
        return if ((currentTime - updatedTime) > FRESH_THRESHOLD_MS) {
            DataState.Stale(updateDate)
        } else {
            DataState.Fresh(updateDate)
        }
    }

    sealed class DataState {
        abstract val date: Date

        class Fresh(override val date: Date) : DataState()
        class Stale(override val date: Date) : DataState()
    }

    private companion object {
        private const val FRESH_THRESHOLD_MS = 1000
    }
}
