package com.mieszko.currencyconverter.presentation.tracking.adapter

import com.airbnb.epoxy.TypedEpoxyController
import com.mieszko.CurrenciesApp
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.domain.model.list.TrackingCurrenciesModel
import com.mieszko.currencyconverter.presentation.tracking.TrackingViewModel
import com.mieszko.currencyconverter.presentation.tracking.adapter.viewholder.trackingListAllItem
import com.mieszko.currencyconverter.presentation.tracking.adapter.viewholder.trackingListHeader

class TrackingListController(private val viewModel: TrackingViewModel) :
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