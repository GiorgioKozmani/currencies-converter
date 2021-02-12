package com.mieszko.currencyconverter.ui.selection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.Typed2EpoxyController
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.data.model.SelectedCurrency
import com.mieszko.currencyconverter.viewmodel.SelectionViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

//todo di
//todo I THINK THAT EPOXY IS THE ONLY WAY TO DO THIS RIGHT
class SelectionFragmentEpoxy : Fragment() {
    private val viewModel by viewModel<SelectionViewModel>()
    private lateinit var epoxyRV: EpoxyRecyclerView
    private val epoxyController: Typed2EpoxyController<List<SelectedCurrency>, List<SelectedCurrency>> by lazy {
        TrackingListController(
            viewModel
        )
    }

    //TODO ADD SHADOW TO YOUR CURRENCIES ON THE BOTTOM / TOP + PADDINGS, SET FIXED HEIGHT OF SELECTED ONES, ADD OPTION FOR DRAG AND DROP ALSO
    // FOR YOUR CURRENCIES
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.epoxy_tracking_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        epoxyRV = view.findViewById(R.id.selection_epoxy_rv)
        epoxyRV.setController(epoxyController)
        epoxyController.setData(listOf(), listOf())
    }

    private fun observeViewModel() {
        viewModel.getTrackedCurrenciesLiveData()
            .observe(viewLifecycleOwner, { updateTrackedCurrenciesList(it) })
    }

    private fun updateTrackedCurrenciesList(items: Pair<List<SelectedCurrency>, List<SelectedCurrency>>) {
        epoxyController.setData(items.first, items.second)

//        trackedCurrenciesAdapter.updateItems(items)
    }
}