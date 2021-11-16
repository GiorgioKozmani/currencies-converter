package com.mieszko.currencyconverter.presentation.selection.adapter

import com.airbnb.epoxy.TypedEpoxyController
import com.mieszko.currencyconverter.domain.model.list.TrackingCurrenciesModel
import com.mieszko.currencyconverter.presentation.selection.adapter.viewholder.trackingListItem

class SelectionListController(private val itemClickAction: (TrackingCurrenciesModel) -> Unit) :
    TypedEpoxyController<List<TrackingCurrenciesModel>>() {

    override fun buildModels(trackingCurrencies: List<TrackingCurrenciesModel>) {

        trackingCurrencies.forEach {
            trackingListItem {
                id(it.code.name)
                model(it)
                clickAction { itemClickAction(it) }
            }
        }
    }
}
