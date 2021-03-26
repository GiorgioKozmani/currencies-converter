package com.mieszko.currencyconverter.presentation.home.header

import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.domain.analytics.IFirebaseEventsLogger
import com.mieszko.currencyconverter.domain.analytics.events.ButtonClickedEvent
import com.mieszko.currencyconverter.presentation.home.HomeViewModel
import com.mieszko.currencyconverter.presentation.util.fadeInText
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.Date

class HomeHeaderFragment : Fragment(R.layout.home_header_fragment) {
    private val eventsLogger: IFirebaseEventsLogger by inject()
    private val viewModel by sharedViewModel<HomeViewModel>()

    private lateinit var loadingIndicator: LinearProgressIndicator
    private lateinit var lastUpdatedTV: TextView
    private lateinit var refreshButton: ImageButton
    private lateinit var refreshInfoButton: ImageButton

    // todo think of the way to handle errors, maybe in parent fragment?
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO VIEWBINDING
        lastUpdatedTV = view.findViewById(R.id.last_updated)
        loadingIndicator = view.findViewById(R.id.loading_indicator)
        refreshButton = view.findViewById(R.id.refresh_rates_button)
        refreshInfoButton = view.findViewById(R.id.refresh_info_button)

        observeViewModel()

        view.findViewById<View>(R.id.refresh_rates_button).setOnClickListener {
            eventsLogger.logEvent(ButtonClickedEvent("refresh_button"))
            viewModel.refreshButtonClicked()
        }

        view.findViewById<View>(R.id.refresh_info_button).setOnClickListener {
            eventsLogger.logEvent(ButtonClickedEvent("info_button"))

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.home_info_dialog_title)
                // TODO STRINGS
                .setMessage("TODO CHANGE THIS TEXT The reference rates are usually updated around 16:00 CET on every working day, except on TARGET closing days. They are based on a regular daily concertation procedure between central banks across Europe, which normally takes place at 14:15 CET.")
                .setPositiveButton(resources.getString(R.string.okay)) { dialog, which ->
                    // Respond to positive button press
                }
                .show()
        }
    }

    private fun observeViewModel() {
        viewModel.getLastUpdatedLiveData()
            .observe(viewLifecycleOwner, { handleNewDate(it) })

        viewModel.getIsLoadingLiveData()
            .observe(viewLifecycleOwner, { setLoadingIndicatorVisibility(it) })
    }

    private fun setLoadingIndicatorVisibility(isLoading: Boolean) {
        if (isLoading) {
            refreshButton.isEnabled = false
            loadingIndicator.show()
        } else {
            refreshButton.isEnabled = true
            loadingIndicator.hide()
        }
    }

    private fun handleNewDate(updateDate: Date) {
        // todo use https://github.com/daimajia/AndroidViewAnimations to bounce in new date or anim myself
        // todo colors after update? for a whole and back to gray
        lastUpdatedTV.fadeInText(
            DateFormat.format("yyyy-MM-dd hh:mm:ss a", updateDate).toString(),
            resources.getInteger(android.R.integer.config_longAnimTime).toLong()
        )
    }
}
