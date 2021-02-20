package com.mieszko.currencyconverter.ui.selection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.Typed2EpoxyController
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.data.model.AllCurrenciesListModel
import com.mieszko.currencyconverter.data.model.TrackedCurrenciesListModel
import com.mieszko.currencyconverter.viewmodel.SelectionViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SelectionFragment : Fragment() {
    private val viewModel by viewModel<SelectionViewModel>()
    private val epoxyController: Typed2EpoxyController<
            List<TrackedCurrenciesListModel>,
            List<AllCurrenciesListModel>> by lazy { TrackingListController(viewModel) }

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
        epoxyController.setData(listOf(), listOf())
    }

    private fun observeViewModel() {
        viewModel.getTrackedCurrenciesLiveData()
            .observe(viewLifecycleOwner, { updateTrackedCurrenciesList(it) })
    }

    private fun updateTrackedCurrenciesList(items: Pair<List<TrackedCurrenciesListModel>, List<AllCurrenciesListModel>>) {
        epoxyController.setData(items.first, items.second)
    }
}