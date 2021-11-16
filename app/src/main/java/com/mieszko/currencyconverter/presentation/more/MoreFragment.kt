package com.mieszko.currencyconverter.presentation.more

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.play.core.review.ReviewManagerFactory
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.data.persistance.ISharedPrefsManager
import com.mieszko.currencyconverter.domain.analytics.IFirebaseEventsLogger
import com.mieszko.currencyconverter.domain.analytics.constants.AnalyticsConstants
import com.mieszko.currencyconverter.domain.analytics.events.MoreTabItemClickedEvent
import com.mieszko.currencyconverter.domain.analytics.events.ScreenViewEvent
import com.mieszko.currencyconverter.presentation.more.list.MoreItemsAdapter
import com.mieszko.currencyconverter.presentation.more.list.MoreListItem
import com.mieszko.currencyconverter.presentation.more.rate.RateAppDialog
import com.mieszko.currencyconverter.presentation.util.FeedbackEmailHelper
import org.koin.android.ext.android.inject

class MoreFragment : Fragment(R.layout.more_fragment) {

    private companion object {
        const val RATE_APP_DIALOG_TAG = "RateAppDialogFragmentTag"
        const val ABOUT_APP_DIALOG_TAG = "AboutAppDialogFragmentTag"
    }

    private val eventsLogger: IFirebaseEventsLogger by inject()
    private val sharedPrefsManager: ISharedPrefsManager by inject()

    private val appVersionTV: TextView by lazy { requireView().findViewById(R.id.app_version) }

    private var startedReviewFlow = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter(
            view,
            mutableListOf<MoreListItem>().apply {
                add(getPrivacyPolicyItem())
                add(getFeedbackItem())
                if (!inAppRateIsMinimum(4)) {
                    add(getRateAppItem())
                }
                add(getAboutItem())
            }
        )

        setupVersionName()
    }

    private fun setupAdapter(view: View, moreItems: List<MoreListItem>) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.more_items_rv)
        recyclerView.adapter = MoreItemsAdapter(moreItems)
    }

    override fun onStart() {
        super.onStart()
        startInAppReviewFlow()
    }

    private fun startInAppReviewFlow() {
        // todo factor out, comment
        if (startedReviewFlow || !inAppRateIsMinimum(4)) {
            return
        }

        startedReviewFlow = true

        val reviewManager = ReviewManagerFactory.create(requireContext())
        val reviewFlowRequest = reviewManager.requestReviewFlow()

        reviewFlowRequest.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                reviewManager.launchReviewFlow(requireActivity(), reviewInfo)
            } else {
                task.exception?.let {
                    eventsLogger.logNonFatalError(it, "START IN-APP-REVIEW FLOW ERROR")
                }
            }
        }
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

                FeedbackEmailHelper.openFeedbackEmail(requireContext())
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

    private fun inAppRateIsMinimum(minimumValue: Int) =
        sharedPrefsManager.getInt(ISharedPrefsManager.Key.InAppRate) >= minimumValue

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
