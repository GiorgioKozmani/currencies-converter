package com.mieszko.currencyconverter.ui.selection

import com.airbnb.epoxy.Typed2EpoxyController
import com.mieszko.CurrenciesApp
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.data.model.AllCurrenciesListModel
import com.mieszko.currencyconverter.data.model.TrackedCurrenciesListModel
import com.mieszko.currencyconverter.ui.selection.adapter.viewholder.trackingListAllItem
import com.mieszko.currencyconverter.ui.selection.adapter.viewholder.trackingListHeader
import com.mieszko.currencyconverter.ui.selection.adapter.viewholder.trackingListItem
import com.mieszko.currencyconverter.viewmodel.SelectionViewModel

class TrackingListController(private val viewModel: SelectionViewModel) :
    Typed2EpoxyController<List<TrackedCurrenciesListModel>, List<AllCurrenciesListModel>>() {

    override fun buildModels(
        trackedCurrencies: List<TrackedCurrenciesListModel>,
        allCurrencies: List<AllCurrenciesListModel>
    ) {

        trackingListHeader {
            id("tr_t")
            title(CurrenciesApp.resourses.getString(R.string.tracked_currencies))
        }

        //todo add no tracked currencies item if no tracked items
        trackedCurrencies.forEach {
            trackingListItem {
                id(it.code.name + "tracked")
                model(it)
                clickAction { viewModel.trackedCurrenciesItemClicked(it) }
            }
        }

        trackingListHeader {
            id("tr_a")
            title(CurrenciesApp.resourses.getString(R.string.all_currencies))
        }

        allCurrencies.forEach {
            trackingListAllItem {
                id(it.code.name + "all")
                model(it)
                clickAction { viewModel.allCurrenciesItemClicked(it) }
            }
        }
    }
}