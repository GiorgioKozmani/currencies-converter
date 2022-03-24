package com.mieszko.currencyconverter.presentation.home.header

import android.os.Bundle
import android.text.format.DateFormat
import android.text.format.DateUtils
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.domain.model.DataUpdatedTime
import com.mieszko.currencyconverter.presentation.home.HomeViewModel
import com.mieszko.currencyconverter.presentation.util.animateInText
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class HomeHeaderFragment : Fragment(R.layout.home_header_fragment) {
    private val viewModel by sharedViewModel<HomeViewModel>()

    private lateinit var loadingIndicator: LinearProgressIndicator
    private lateinit var lastUpdatedTV: TextView
    private lateinit var refreshButton: ImageButton
    private lateinit var infoButton: ImageButton

    // [FUTURE IMPROVEMENT] https://material.io/components/snackbars
    // OKAY OPTION + EXPLANATION THAT WE USE OLD DATA FOR NOW AND YOU CAN CLICK REFRESH BUTTON TO TRY AGAIN
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        assignViews(view)

        observeViewModel()

        refreshButton.setOnClickListener { viewModel.refreshButtonClicked() }

        infoButton.setOnClickListener {
            viewModel.infoButtonClicked()

            // [FUTURE IMPROVEMENT] Introduce proper STATE mechanism, move that to VM.
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.home_info_dialog_title)
                .setMessage(R.string.refresh_info)
                .setPositiveButton(resources.getString(R.string.got_it)) { _, _ -> }
                .show()
        }
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

    private fun handleNewDate(dataUpdatedTime: DataUpdatedTime) {
        val dateState = dataUpdatedTime.getDataState()

        val isRefreshedToday = DateUtils.isToday(dateState.date.time)

        val dateText = if (isRefreshedToday) {
            getString(R.string.today)
        } else {
            DateFormat.format("yyyy-MM-dd", dateState.date).toString()
        } + "!"

        when (dateState) {
            is DataUpdatedTime.DataState.Fresh -> {
                lastUpdatedTV.animateInText(
                    dateText,
                    resources.getInteger(android.R.integer.config_longAnimTime).toLong()
                )
            }
            is DataUpdatedTime.DataState.Stale -> {
                lastUpdatedTV.text = dateText
            }
        }
    }

    private fun observeViewModel() {
        viewModel.getLastUpdatedLiveData()
            .observe(viewLifecycleOwner) { handleNewDate(it) }

        viewModel.getIsLoadingLiveData()
            .observe(viewLifecycleOwner) { setLoadingIndicatorVisibility(it) }
    }

    private fun assignViews(view: View) {
        lastUpdatedTV = view.findViewById(R.id.last_updated)
        loadingIndicator = view.findViewById(R.id.loading_indicator)
        refreshButton = view.findViewById(R.id.refresh_rates_button)
        infoButton = view.findViewById(R.id.refresh_info_button)
    }
}
