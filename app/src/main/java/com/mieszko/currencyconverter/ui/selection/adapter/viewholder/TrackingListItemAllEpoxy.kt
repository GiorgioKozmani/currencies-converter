package com.mieszko.currencyconverter.ui.selection.adapter.viewholder

import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.common.SupportedCurrency
import de.hdodenhof.circleimageview.CircleImageView

@EpoxyModelClass(layout = R.layout.epoxy_tracking_list_all_item)
abstract class TrackingListAllItemEpoxy : EpoxyModelWithHolder<TrackingListAllItemViewHolder>() {

    @EpoxyAttribute
    lateinit var currencyItem: SupportedCurrency

    //TODO HomeListItem should contain that information already! Remove
    @EpoxyAttribute
    open var isSelected: Boolean = false

    //todo this should be contained in model
    //   todo investigate @EpoxyAttribute(DoNotHash)
    @EpoxyAttribute
    lateinit var clickAction: () -> Unit

    override fun bind(holder: TrackingListAllItemViewHolder) {
        with(currencyItem) {
            setFullNameText(holder, fullName)
            handleSelection(holder, isSelected)
            setShortNameText(holder, name)
            loadCurrencyFlag(holder, flagUrl)
        }

        //todo investigate DONOTHASH
        holder.view.setOnClickListener { clickAction.invoke() }
    }

    override fun unbind(holder: TrackingListAllItemViewHolder) {
        // Release resources and don't leak listeners as this view goes back to the view pool
//        holder.button.setOnClickListener(null)
//        holder.button.setImageDrawable(null)
    }

    private fun handleSelection(holder: TrackingListAllItemViewHolder, isSelected: Boolean) {
        //todo implement correctly
        val testColor = if (isSelected) {
            ContextCompat.getColor(holder.view.context, R.color.colorAccent)
        } else {
            ContextCompat.getColor(holder.view.context, R.color.colorPrimary)
        }
        holder.selectedCheckbox.background = ColorDrawable(testColor)
    }

    private fun setFullNameText(
        holder: TrackingListAllItemViewHolder,
        @StringRes currencyFullNameRes: Int
    ) {
        holder.fullNameTV.text = holder.view.context.getString(currencyFullNameRes)
    }

    private fun setShortNameText(holder: TrackingListAllItemViewHolder, currencyShortName: String) {
        holder.shortNameTV.text = currencyShortName
    }

    private fun loadCurrencyFlag(holder: TrackingListAllItemViewHolder, flagUrl: String) {
        Glide.with(holder.view.context)
            .load(flagUrl)
            .apply(RequestOptions().apply { centerCrop() })
            .into(holder.flagIV)
    }
}

class TrackingListAllItemViewHolder : EpoxyHolder() {
    lateinit var flagIV: CircleImageView
    lateinit var selectedCheckbox: ImageView
    lateinit var fullNameTV: TextView
    lateinit var shortNameTV: TextView

    lateinit var view: View

    override fun bindView(itemView: View) {
        view = itemView

        selectedCheckbox = itemView.findViewById(R.id.selected_checkbox)
        fullNameTV = itemView.findViewById(R.id.currency_full_name)
        shortNameTV = itemView.findViewById(R.id.currency_short_name)
        flagIV = itemView.findViewById(R.id.country_flag)
    }
}