package com.mieszko.currencyconverter.presentation.more

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.domain.analytics.IFirebaseEventsLogger
import com.mieszko.currencyconverter.domain.analytics.constants.AnalyticsConstants
import com.mieszko.currencyconverter.domain.analytics.events.MoreTabItemClickedEvent
import com.mieszko.currencyconverter.domain.analytics.events.ScreenViewEvent
import com.mieszko.currencyconverter.presentation.more.list.MoreItemsAdapter
import com.mieszko.currencyconverter.presentation.more.list.MoreListItem
import com.mieszko.currencyconverter.presentation.util.EmailHelper
import org.koin.android.ext.android.inject

class MoreFragment : Fragment(R.layout.more_fragment) {

    private companion object {
        const val RATE_APP_DIALOG_TAG = "RateAppDialogFragmentTag"
        const val ABOUT_APP_DIALOG_TAG = "AboutAppDialogFragmentTag"
    }

    private val eventsLogger: IFirebaseEventsLogger by inject()
    private val appVersionTV: TextView by lazy { requireView().findViewById(R.id.app_version) }

// TODO PROGUARD! OR R8?
    // https://firebase.google.com/support/guides/launch-checklist?authuser=0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // todo events logging for this
        setupAdapter(
            view,
            listOf(
                // todo maybe is not needed, read google requirements
                getPrivacyPolicyItem(),
                getFeedbackItem(),
                // TODO Trigger 1 is activated after the customer opens the app for a certain number times in a day
                // TODO Trigger 2 requires the customer use the app at least 3 times
                // TODO INTRODUCE 2 STEP WITH FIRST THUMB UP / DOWN (or faces in a scale of satisfaction 1 - 5) + LOGGING. If thumbed up then ask for review online
                getRateAppItem(),
                getAboutItem()
            )
        )

        setupVersionName()
    }

    private fun setupAdapter(view: View, moreItems: List<MoreListItem>) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.more_items_rv)
        recyclerView.adapter = MoreItemsAdapter(moreItems)
    }

    override fun onResume() {
        super.onResume()
        eventsLogger.logEvent(ScreenViewEvent(AnalyticsConstants.Events.ScreenView.Screen.MORE))
    }

    private fun getAboutItem() =
        MoreListItem.Builder(requireContext())
            .setTitle(R.string.about_title)
            .setDescription(R.string.about_desc)
            .setIcon(R.drawable.round_emoji_people_black_48)
            .doOnClick {
                eventsLogger.logEvent(MoreTabItemClickedEvent(AnalyticsConstants.Events.MoreTabItemClicked.MoreItem.ABOUT))

                AboutAppDialog().show(childFragmentManager, ABOUT_APP_DIALOG_TAG)
            }
            .build()

    private fun getRateAppItem(): MoreListItem {
        return MoreListItem.Builder(requireContext())
            .setTitle(R.string.rate_app_title)
            .setDescription(R.string.rate_app_description)
            .setIcon(R.drawable.outline_thumbs_up_down_black_48)
            .doOnClick {
                eventsLogger.logEvent(MoreTabItemClickedEvent(AnalyticsConstants.Events.MoreTabItemClicked.MoreItem.RATE))

                RateAppDialog().show(childFragmentManager, RATE_APP_DIALOG_TAG)
            }
            .build()
    }

    private fun getFeedbackItem() =
        MoreListItem.Builder(requireContext())
            .setTitle(R.string.leave_feedback_title)
            .setDescription(R.string.leave_feedback_desc)
            .setIcon(R.drawable.outline_wb_incandescent_black_48)
            .doOnClick {
                eventsLogger.logEvent(MoreTabItemClickedEvent(AnalyticsConstants.Events.MoreTabItemClicked.MoreItem.FEEDBACK))

                EmailHelper.openFeedbackEmail(requireContext())
            }
            .build()

    private fun getPrivacyPolicyItem() =
        MoreListItem.Builder(requireContext())
            .setTitle(R.string.privacy_policy_title)
            .setDescription(R.string.privacy_policy_desc)
            .setIcon(R.drawable.outline_policy_black_48)
            .doOnClick {
                eventsLogger.logEvent(MoreTabItemClickedEvent(AnalyticsConstants.Events.MoreTabItemClicked.MoreItem.PRIVACY))

                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://your-currency-conver.flycricket.io/privacy.html")
                    )
                )
            }
            .build()

    private fun setupVersionName() {
        try {
            val packageInfo = requireContext().run { packageManager.getPackageInfo(packageName, 0) }
            val versionText = packageInfo.versionName
            val appName = getString(R.string.app_name)

            appVersionTV.text = "$appName $versionText"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }
}
