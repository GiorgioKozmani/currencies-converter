package com.mieszko.currencyconverter.presentation.tracking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.TypedEpoxyController
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.domain.model.list.TrackingCurrenciesModel
import com.mieszko.currencyconverter.presentation.tracking.adapter.TrackingListController
import org.koin.androidx.viewmodel.ext.android.viewModel

class TrackingFragment : Fragment() {

    private val viewModel by viewModel<TrackingViewModel>()
    private val epoxyController: TypedEpoxyController<List<TrackingCurrenciesModel>> by lazy {
        TrackingListController(
            viewModel
        )
    }

    private val searchView: SearchView by lazy { requireView().findViewById(R.id.currency_search_view) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.epoxy_tracking_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        view.findViewById<EpoxyRecyclerView>(R.id.selection_epoxy_rv).setController(epoxyController)

        // TODO THINK OF INITIAL DATA
        epoxyController.setData(listOf())

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    viewModel.searchQueryChanged(newText)
                }

                //todo think of suggestions
                return true
            }
        })
    }

    private fun observeViewModel() {
        viewModel.getTrackedCurrenciesLiveData()
            .observe(viewLifecycleOwner, { updateTrackedCurrenciesList(it) })
    }

    private fun updateTrackedCurrenciesList(items: List<TrackingCurrenciesModel>) {
        epoxyController.setData(items)
    }
}