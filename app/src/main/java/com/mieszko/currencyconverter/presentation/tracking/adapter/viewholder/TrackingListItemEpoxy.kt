package com.mieszko.currencyconverter.presentation.tracking.adapter.viewholder

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
import com.mieszko.currencyconverter.domain.model.TrackedCurrenciesListModel
import de.hdodenhof.circleimageview.CircleImageView

@EpoxyModelClass(layout = R.layout.epoxy_tracking_list_item)
abstract class TrackingListItemEpoxy : EpoxyModelWithHolder<TrackingListItemViewHolder>() {

    @EpoxyAttribute
    lateinit var model: TrackedCurrenciesListModel

    //todo this should be contained in model
    //   todo investigate @EpoxyAttribute(DoNotHash)
    @EpoxyAttribute
    lateinit var clickAction: () -> Unit

    override fun bind(holder: TrackingListItemViewHolder) {
        with(model) {
            setCodeText(holder, code.name)
            setNameText(holder, codeData.name)
            loadCurrencyFlag(holder, codeData.flagUrl)
        }

        holder.view.setOnClickListener { clickAction.invoke() }
    }

    override fun unbind(holder: TrackingListItemViewHolder) {
        // todo implement
        // Release resources and don't leak listeners as this view goes back to the view pool
//        holder.button.setOnClickListener(null)
//        holder.button.setImageDrawable(null)
    }

    private fun setNameText(
        holder: TrackingListItemViewHolder,
        @StringRes currencyNameRes: Int
    ) {
        holder.nameTV.text = holder.view.context.getString(currencyNameRes)
    }

    private fun setCodeText(holder: TrackingListItemViewHolder, code: String) {
        holder.codeTV.text = code
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
    lateinit var nameTV: TextView
    lateinit var codeTV: TextView

    lateinit var view: View

    override fun bindView(itemView: View) {
        view = itemView

        nameTV = itemView.findViewById(R.id.currency_full_name)
        codeTV = itemView.findViewById(R.id.currency_short_name)
        flagIV = itemView.findViewById(R.id.country_flag)
    }
}