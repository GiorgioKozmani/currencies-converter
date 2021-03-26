package com.mieszko.currencyconverter.presentation.selection

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.TypedEpoxyController
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.domain.analytics.IFirebaseEventsLogger
import com.mieszko.currencyconverter.domain.analytics.constants.AnalyticsConstants
import com.mieszko.currencyconverter.domain.analytics.events.ScreenViewEvent
import com.mieszko.currencyconverter.domain.model.list.TrackingCurrenciesModel
import com.mieszko.currencyconverter.presentation.selection.adapter.TrackingListController
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class SelectionFragment : Fragment(R.layout.selection_fragment) {
    private val eventsLogger: IFirebaseEventsLogger by inject()

    override fun onResume() {
        super.onResume()
        eventsLogger.logEvent(ScreenViewEvent(AnalyticsConstants.Events.ScreenView.Screen.SELECTION))
    }

    // TODO CONSIDER MATERIAL SEARCHVIEW
    // todo first back click clears searchview
    private val viewModel by viewModel<SelectionViewModel>()
    private val epoxyController: TypedEpoxyController<List<TrackingCurrenciesModel>> by lazy {
        TrackingListController(viewModel)
    }

    private val searchView: SearchView by lazy { requireView().findViewById(R.id.currency_search_view) }
    private val epoxyRV: EpoxyRecyclerView by lazy { requireView().findViewById(R.id.selection_epoxy_rv) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        epoxyRV.setController(epoxyController)

        // TODO THINK OF INITIAL DATA
        epoxyController.run {
            setData(listOf())
            addModelBuildListener {
                epoxyRV.scrollToPosition(0)
            }
        }

        setupSearchView()
    }

    private fun setupSearchView() {
        searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    // perform default action
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText != null) {
                        viewModel.searchQueryChanged(newText)
                    }

                    return true
                }
            })

            setOnQueryTextFocusChangeListener { v, hasFocus ->
                if (!hasFocus) {
                    setQuery("", true)
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.getTrackedCurrenciesLiveData()
            .observe(viewLifecycleOwner, { updateTrackedCurrenciesList(it) })
    }

    private fun updateTrackedCurrenciesList(items: List<TrackingCurrenciesModel>) {
        epoxyController.setData(items)
    }
}
