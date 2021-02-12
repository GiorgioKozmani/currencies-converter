package com.mieszko.currencyconverter.ui.selection

import com.airbnb.epoxy.Typed2EpoxyController
import com.mieszko.CurrenciesApp
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.data.model.SelectedCurrency
import com.mieszko.currencyconverter.ui.selection.adapter.viewholder.trackingListAllItem
import com.mieszko.currencyconverter.ui.selection.adapter.viewholder.trackingListHeader
import com.mieszko.currencyconverter.ui.selection.adapter.viewholder.trackingListItem
import com.mieszko.currencyconverter.viewmodel.SelectionViewModel

class TrackingListController(private val viewModel: SelectionViewModel) :
    Typed2EpoxyController<List<SelectedCurrency>, List<SelectedCurrency>>() {

    override fun buildModels(
        trackedCurrencies: List<SelectedCurrency>,
        allCurrencies: List<SelectedCurrency>
    ) {

        trackingListHeader {
            id("tr_t")
            title(CurrenciesApp.resourses.getString(R.string.tracked_currencies))
        }

        //todo add no tracked currencies item if no tracked items

        trackedCurrencies.forEach {
            trackingListItem {
                id(it.currency.name + "tracked")
                currencyItem(it.currency)
                isSelected(it.isTracked)
                clickAction { viewModel.trackedCurrenciesItemClicked(it) }
            }
        }

        trackingListHeader {
            id("tr_a")
            title(CurrenciesApp.resourses.getString(R.string.all_currencies))
        }

        allCurrencies.forEach {
            trackingListAllItem {
                id(it.currency.name + "all")
                currencyItem(it.currency)
                isSelected(it.isTracked)
                clickAction { viewModel.allCurrenciesItemClicked(it) }
            }
        }
    }
}