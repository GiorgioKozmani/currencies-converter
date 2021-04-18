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
import com.mieszko.currencyconverter.domain.model.UpdateDate
import com.mieszko.currencyconverter.presentation.home.HomeViewModel
import com.mieszko.currencyconverter.presentation.util.fadeInText
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class HomeHeaderFragment : Fragment(R.layout.home_header_fragment) {
    private val eventsLogger: IFirebaseEventsLogger by inject()
    private val viewModel by sharedViewModel<HomeViewModel>()

    private lateinit var loadingIndicator: LinearProgressIndicator
    private lateinit var lastUpdatedTV: TextView
    private lateinit var refreshButton: ImageButton
    private lateinit var refreshInfoButton: ImageButton

    //TODO
//   https://material.io/components/snackbars
    // OKAY  OPTION + EXPLANATION THAT WE USE OLD DATA FOR NOW AND YOU CAN CLICK REFRESH BUTTON TO TRY AGAIN

    // todo think of the way to handle errors, maybe in parent fragment?
    // TODO THINK OF INTRUDUCING X SECONDS AGO / TODAY AT / YESTERDAY AT / FULL DATE
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
                .setMessage(R.string.refresh_info)
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

    private fun handleNewDate(updateDate: UpdateDate) {
        val dateState = updateDate.getDateState()
        val dateText = DateFormat.format("yyyy-MM-dd hh:mm:ss a", dateState.date).toString()

        when (dateState) {
            is UpdateDate.StatefulDate.Fresh -> {
                lastUpdatedTV.fadeInText(
                    dateText,
                    resources.getInteger(android.R.integer.config_longAnimTime).toLong()
                )
            }
            is UpdateDate.StatefulDate.Stale -> {
                lastUpdatedTV.text = dateText
            }
        }
    }
}
