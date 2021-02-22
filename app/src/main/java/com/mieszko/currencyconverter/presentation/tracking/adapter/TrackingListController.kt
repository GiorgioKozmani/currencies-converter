package com.mieszko.currencyconverter.presentation.tracking.adapter

import com.airbnb.epoxy.Typed2EpoxyController
import com.mieszko.CurrenciesApp
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.domain.model.AllCurrenciesListModel
import com.mieszko.currencyconverter.domain.model.TrackedCurrenciesListModel
import com.mieszko.currencyconverter.presentation.tracking.TrackingViewModel
import com.mieszko.currencyconverter.presentation.tracking.adapter.viewholder.trackingListAllItem
import com.mieszko.currencyconverter.presentation.tracking.adapter.viewholder.trackingListHeader
import com.mieszko.currencyconverter.presentation.tracking.adapter.viewholder.trackingListItem

class TrackingListController(private val viewModel: TrackingViewModel) :
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