package com.mieszko.currencyconverter.ui.selection.adapter.viewholder

import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.common.SupportedCurrency
import de.hdodenhof.circleimageview.CircleImageView

@EpoxyModelClass(layout = R.layout.epoxy_tracking_list_item)
abstract class TrackingListItemEpoxy : EpoxyModelWithHolder<TrackingListItemViewHolder>() {

    @EpoxyAttribute
    lateinit var currencyItem: SupportedCurrency

    //todo this should be contained in model
    //   todo investigate @EpoxyAttribute(DoNotHash)
    @EpoxyAttribute
    lateinit var clickAction: () -> Unit

    override fun bind(holder: TrackingListItemViewHolder) {
        with(currencyItem) {
            setFullNameText(holder, fullName)
            setShortNameText(holder, name)
            loadCurrencyFlag(holder, flagUrl)
        }

        holder.view.setOnClickListener { clickAction.invoke() }
    }

    override fun unbind(holder: TrackingListItemViewHolder) {
        // Release resources and don't leak listeners as this view goes back to the view pool
//        holder.button.setOnClickListener(null)
//        holder.button.setImageDrawable(null)
    }

    private fun setFullNameText(
        holder: TrackingListItemViewHolder,
        @StringRes currencyFullNameRes: Int
    ) {
        holder.fullNameTV.text = holder.view.context.getString(currencyFullNameRes)
    }

    private fun setShortNameText(holder: TrackingListItemViewHolder, currencyShortName: String) {
        holder.shortNameTV.text = currencyShortName
    }

    private fun loadCurrencyFlag(holder: TrackingListItemViewHolder, flagUrl: String) {
        Glide.with(holder.view.context)
            .load(flagUrl)
            .apply(RequestOptions().apply { centerCrop() })
            .into(holder.flagIV)
    }
}

class TrackingListItemViewHolder : EpoxyHolder() {
    lateinit var flagIV: CircleImageView
    lateinit var fullNameTV: TextView
    lateinit var shortNameTV: TextView

    lateinit var view: View

    override fun bindView(itemView: View) {
        view = itemView

        fullNameTV = itemView.findViewById(R.id.currency_full_name)
        shortNameTV = itemView.findViewById(R.id.currency_short_name)
        flagIV = itemView.findViewById(R.id.country_flag)
    }
}