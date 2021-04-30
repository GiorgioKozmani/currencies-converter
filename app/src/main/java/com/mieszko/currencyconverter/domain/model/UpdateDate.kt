package com.mieszko.currencyconverter.domain.model

import java.util.Date

data class UpdateDate(private val date: Date) {
    private val freshThreshold = 1000

    fun getDateState(): StatefulDate {
        return if ((Date().time - date.time) > freshThreshold) {
            StatefulDate.Stale(date)
        } else {
            StatefulDate.Fresh(date)
        }
    }

    sealed class StatefulDate {
        abstract val date: Date

        class Fresh(override val date: Date) : StatefulDate()
        class Stale(override val date: Date) : StatefulDate()
    }
}
