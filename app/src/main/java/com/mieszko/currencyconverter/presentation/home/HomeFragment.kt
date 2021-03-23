package com.mieszko.currencyconverter.presentation.home

import androidx.fragment.app.Fragment
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.domain.analytics.IFirebaseEventsLogger
import com.mieszko.currencyconverter.domain.analytics.common.events.ScreenViewEvent
import com.mieszko.currencyconverter.domain.analytics.constants.AnalyticsConstants
import org.koin.android.ext.android.inject

class HomeFragment : Fragment(R.layout.home_fragment) {
    private val eventsLogger: IFirebaseEventsLogger by inject()

    override fun onResume() {
        super.onResume()
        eventsLogger.logEvent(ScreenViewEvent(AnalyticsConstants.Events.ScreenView.Screen.HOME))
    }
}
