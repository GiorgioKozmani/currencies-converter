package com.mieszko.currencyconverter.presentation.selection.adapter

import com.airbnb.epoxy.TypedEpoxyController
import com.mieszko.currencyconverter.domain.model.list.TrackingCurrenciesModel
import com.mieszko.currencyconverter.presentation.selection.SelectionViewModel
import com.mieszko.currencyconverter.presentation.selection.adapter.viewholder.trackingListAllItem

class TrackingListController(private val viewModel: SelectionViewModel) :
    TypedEpoxyController<List<TrackingCurrenciesModel>>() {

    override fun buildModels(trackingCurrencies: List<TrackingCurrenciesModel>) {

//        trackingListHeader {
//            id("tr_a")
//            title(CurrenciesApp.resourses.getString(R.string.all_currencies))
//        }

        trackingCurrencies.forEach {
            trackingListAllItem {
                id(it.code.name)
                model(it)
                clickAction { viewModel.allCurrenciesItemClicked(it) }
            }
        }
    }
}