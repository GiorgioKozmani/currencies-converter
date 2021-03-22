package com.mieszko.currencyconverter.presentation.more

import androidx.fragment.app.Fragment
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.domain.analytics.IFirebaseEventsLogger
import com.mieszko.currencyconverter.domain.analytics.common.events.ScreenViewEvent
import com.mieszko.currencyconverter.domain.analytics.constants.AnalyticsConstants
import org.koin.android.ext.android.inject

class MoreFragment : Fragment(R.layout.more_fragment) {

    private val eventsLogger: IFirebaseEventsLogger by inject()

    override fun onResume() {
        super.onResume()
        eventsLogger.logEvent(ScreenViewEvent(AnalyticsConstants.Events.ScreenView.Screen.MORE))
    }

}
//TODO IDEAS
// RATE APP + SEPARATE LEAVE FEEDBACK / REPORT PROBLEMS
// SHARE APP
// SEARCH ICON INSTEAD OF EURO ,EURO FOR HOME PAGE?
// CONSIDER USING GRID WITH ITEMS + LITTLE DESCRIPTION INSTEAD OF SIMPLE LIST AS THERE WILL BE LITTLE ITEMS
// TODO NICE IDEA!
// HAVE CALCULATOR IN THE MIDDLE, AND BOTH MORE AND CURRENCIES ON SIDES (IN BOTTOM NAVIGATION) THEN USE EURO FOR HOMELIST AND LOUPE FOR SEARCH

//todo look for some lottie animations, maybe something would fit

//TODO PROGUARD! OR R8?

//https://firebase.google.com/support/guides/launch-checklist?authuser=0