package com.mieszko.currencyconverter.presentation.selection.adapter.viewholder

import android.view.View
import android.widget.TextView
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.mieszko.currencyconverter.R

@EpoxyModelClass(layout = R.layout.epoxy_tracking_list_title_item)
abstract class TrackingListHeaderEpoxy : EpoxyModelWithHolder<ListItemTitleViewHolder>() {

    @EpoxyAttribute
    var title: String = ""

    override fun bind(holder: ListItemTitleViewHolder) {
        holder.title.text = title
    }
}

class ListItemTitleViewHolder : EpoxyHolder() {
    lateinit var title: TextView

    override fun bindView(itemView: View) {
        title = itemView.findViewById(R.id.title)
    }
}