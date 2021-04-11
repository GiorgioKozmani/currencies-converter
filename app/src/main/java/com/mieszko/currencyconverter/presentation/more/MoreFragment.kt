package com.mieszko.currencyconverter.presentation.more

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.domain.analytics.IFirebaseEventsLogger
import com.mieszko.currencyconverter.domain.analytics.constants.AnalyticsConstants
import com.mieszko.currencyconverter.domain.analytics.events.ScreenViewEvent
import com.mieszko.currencyconverter.presentation.more.list.MoreItemsAdapter
import com.mieszko.currencyconverter.presentation.more.list.MoreListItem
import com.mieszko.currencyconverter.presentation.util.EmailHelper
import com.mieszko.currencyconverter.presentation.util.RateAppWidget
import org.koin.android.ext.android.inject


class MoreFragment : Fragment(R.layout.more_fragment) {

    private val eventsLogger: IFirebaseEventsLogger by inject()
    private val appVersionTV: TextView by lazy { requireView().findViewById(R.id.app_version) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // todo events logging for this
        setupAdapter(
            view, listOf(
                MoreListItem.Builder(requireContext()).setTitle(R.string.privacy_policy_title)
                    .setDescription(
                        R.string.privacy_policy_desc
                    ).setIcon(R.drawable.outline_policy_black_48).doOnClick {}.build(),
                MoreListItem.Builder(requireContext()).setTitle(R.string.leave_feedback_title)
                    .setDescription(
                        R.string.leave_feedback_desc
                    ).setIcon(R.drawable.outline_wb_incandescent_black_48).doOnClick {
                        EmailHelper.openFeedbackEmail(
                            requireContext()
                        )
                    }.build(),
                // TODO FOR THE RATING SHOWING WE SHOULD CONSIDER
                //  Trigger 1 is activated after the customer opens the app for a certain number times in a day
                // TODO Trigger 2 requires the customer use the app at least 3 times
                // TODO INTRODUCE 2 STEP WITH FIRST THUMB UP / DOWN (or faces in a scale of satisfaction 1 - 5) + LOGGING. If thumbed up then ask for review online
                MoreListItem.Builder(requireContext()).setTitle(R.string.rate_app_title)
                    .setDescription(
                        R.string.rate_app_description
                    ).setIcon(R.drawable.outline_thumbs_up_down_black_48).doOnClick {
                        RateAppWidget().show(childFragmentManager, "RateAppFragment")
                    }.build(),
                MoreListItem.Builder(requireContext()).setTitle(R.string.about_title)
                    .setDescription(
                        R.string.about_desc
                    ).setIcon(R.drawable.outline_emoji_people_black_48).doOnClick {}.build()
            )
        )

        setupVersionName()
    }

    private fun setupVersionName() {
        try {
            val pInfo = requireContext().run { packageManager.getPackageInfo(packageName, 0) }
            appVersionTV.text = "TODO APP NAME, v" + pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun setupAdapter(view: View, moreItems: List<MoreListItem>) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.more_items_rv)
        recyclerView.adapter = MoreItemsAdapter(moreItems)
    }

    override fun onResume() {
        super.onResume()
        eventsLogger.logEvent(ScreenViewEvent(AnalyticsConstants.Events.ScreenView.Screen.MORE))
    }
}
// TODO IDEAS
// RATE APP + SEPARATE LEAVE FEEDBACK / REPORT PROBLEMS
// SHARE APP
// SEARCH ICON INSTEAD OF EURO ,EURO FOR HOME PAGE?
// CONSIDER USING GRID WITH ITEMS + LITTLE DESCRIPTION INSTEAD OF SIMPLE LIST AS THERE WILL BE LITTLE ITEMS
// TODO NICE IDEA!
// HAVE CALCULATOR IN THE MIDDLE, AND BOTH MORE AND CURRENCIES ON SIDES (IN BOTTOM NAVIGATION) THEN USE EURO FOR HOMELIST AND LOUPE FOR SEARCH

// todo look for some lottie animations, maybe something would fit

// TODO PROGUARD! OR R8?

// https://firebase.google.com/support/guides/launch-checklist?authuser=0
