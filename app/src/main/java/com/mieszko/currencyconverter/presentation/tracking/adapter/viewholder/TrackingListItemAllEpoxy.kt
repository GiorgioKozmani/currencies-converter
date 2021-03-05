package com.mieszko.currencyconverter.presentation.tracking.adapter.viewholder

import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.domain.model.list.AllCurrenciesListModel
import de.hdodenhof.circleimageview.CircleImageView

@EpoxyModelClass(layout = R.layout.epoxy_tracking_list_all_item)
abstract class TrackingListAllItemEpoxy : EpoxyModelWithHolder<TrackingListAllItemViewHolder>() {

    @EpoxyAttribute
    lateinit var model: AllCurrenciesListModel

    //todo this should be contained in model
    //   todo investigate @EpoxyAttribute(DoNotHash)
    @EpoxyAttribute
    lateinit var clickAction: () -> Unit

    override fun bind(holder: TrackingListAllItemViewHolder) {
        with(model) {
            setCodeText(holder, code.name)
            setNameText(holder, codeData.name)
            handleSelection(holder, isTracked)
            loadCurrencyFlag(holder, codeData.flagResId)
        }

        //todo investigate DONOTHASH
        holder.view.setOnClickListener { clickAction.invoke() }
    }

    override fun unbind(holder: TrackingListAllItemViewHolder) {
        //todo implement
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

    private fun setNameText(
        holder: TrackingListAllItemViewHolder,
        currencyName: String
    ) {
        holder.nameTV.text = currencyName
    }

    private fun setCodeText(holder: TrackingListAllItemViewHolder, currencyCode: String) {
        holder.codeTV.text = currencyCode
    }

    private fun loadCurrencyFlag(holder: TrackingListAllItemViewHolder, @DrawableRes flagRes: Int) {
        Glide.with(holder.view.context)
            .load(flagRes)
            .apply(RequestOptions().apply { centerCrop() })
            .into(holder.flagIV)
    }
}

class TrackingListAllItemViewHolder : EpoxyHolder() {
    lateinit var flagIV: CircleImageView
    lateinit var selectedCheckbox: ImageView
    lateinit var nameTV: TextView
    lateinit var codeTV: TextView

    lateinit var view: View

    override fun bindView(itemView: View) {
        view = itemView

        selectedCheckbox = itemView.findViewById(R.id.selected_checkbox)
        nameTV = itemView.findViewById(R.id.currency_full_name)
        codeTV = itemView.findViewById(R.id.currency_short_name)
        flagIV = itemView.findViewById(R.id.country_flag)
    }
}