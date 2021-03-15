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
        TrackingListController(viewModel)
    }

    //TODO REMOVE BACK ARROW?
    private val backArrow: View by lazy { requireView().findViewById(R.id.back_arrow_btn) }
    private val searchView: SearchView by lazy { requireView().findViewById(R.id.currency_search_view) }
    private val epoxyRV: EpoxyRecyclerView by lazy { requireView().findViewById(R.id.selection_epoxy_rv) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.epoxy_tracking_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        backArrow.setOnClickListener {
            activity?.onBackPressed()
        }

        observeViewModel()
        epoxyRV.setController(epoxyController)

        // TODO THINK OF INITIAL DATA
        epoxyController.run {
            setData(listOf())
            addModelBuildListener {
                epoxyRV.scrollToPosition(0)
            }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
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
    }

    private fun observeViewModel() {
        viewModel.getTrackedCurrenciesLiveData()
            .observe(viewLifecycleOwner, { updateTrackedCurrenciesList(it) })
    }

    private fun updateTrackedCurrenciesList(items: List<TrackingCurrenciesModel>) {
        epoxyController.setData(items)
    }
}