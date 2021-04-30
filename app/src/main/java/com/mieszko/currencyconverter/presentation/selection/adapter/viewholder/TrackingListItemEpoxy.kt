package com.mieszko.currencyconverter.presentation.selection.adapter.viewholder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyHolder
import com.airbnb.epoxy.EpoxyModel
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.domain.model.list.TrackingCurrenciesModel

@EpoxyModelClass(layout = R.layout.epoxy_tracking_list_item)
abstract class TrackingListItemEpoxy : EpoxyModelWithHolder<TrackingListItemViewHolder>() {

    @EpoxyAttribute
    lateinit var model: TrackingCurrenciesModel

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    lateinit var clickAction: () -> Unit

    override fun bind(holder: TrackingListItemViewHolder) {
        with(model) {
            setCodeText(holder, code.name)
            setNameText(holder, codeStaticData.name)
            setSelectionIcon(holder, isTracked)
            loadCurrencyFlag(holder, codeStaticData.flagResId)
        }

        holder.view.setOnClickListener { clickAction.invoke() }
    }

    override fun bind(holder: TrackingListItemViewHolder, previouslyBoundModel: EpoxyModel<*>) {
        if (previouslyBoundModel is TrackingListItemEpoxy && model.isTracked != previouslyBoundModel.model.isTracked) {
            animateSelectionChange(holder, model.isTracked)
            holder.view.setOnClickListener { clickAction.invoke() }
        } else {
            super.bind(holder, previouslyBoundModel)
        }
    }

    override fun unbind(holder: TrackingListItemViewHolder) {
        // Release resources and don't leak listeners as this view goes back to the view pool
        holder.view.setOnClickListener(null)
    }

    private fun animateSelectionChange(holder: TrackingListItemViewHolder, isSelected: Boolean) {
        if (isSelected) {
            holder.animateSelected()
        } else {
            holder.animateUnselected()
        }
    }

    private fun setSelectionIcon(holder: TrackingListItemViewHolder, isSelected: Boolean) {
        with(holder.selectedCheckbox) {
            if (isSelected) {
                scaleX = 1f
                scaleY = 1f
            } else {
                scaleX = 0f
                scaleY = 0f
            }
        }
    }

    private fun setNameText(
        holder: TrackingListItemViewHolder,
        currencyName: String
    ) {
        holder.nameTV.text = currencyName
    }

    private fun setCodeText(holder: TrackingListItemViewHolder, currencyCode: String) {
        holder.codeTV.text = currencyCode
    }

    private fun loadCurrencyFlag(holder: TrackingListItemViewHolder, @DrawableRes flagRes: Int) {
        Glide.with(holder.view.context)
            .load(flagRes)
            .apply(RequestOptions().apply { centerCrop() })
            .into(holder.flagIV)
    }
}

class TrackingListItemViewHolder : EpoxyHolder() {
    lateinit var flagIV: ImageView
    lateinit var selectedCheckbox: ImageView
    lateinit var nameTV: TextView
    lateinit var codeTV: TextView

    lateinit var view: View

    private val shortAnimTime = 150L
    private val mediumAnimTime = 200L

    fun animateSelected() {
        selectedCheckbox.animate()
            .scaleX(1.3f)
            .scaleY(1.3f)
            .setDuration(mediumAnimTime)
            .withEndAction {
                selectedCheckbox.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .duration = shortAnimTime
            }
    }

    fun animateUnselected() {
        selectedCheckbox.animate()
            .scaleX(1.3f)
            .scaleY(1.3f)
            .setDuration(mediumAnimTime)
            .withEndAction {
                selectedCheckbox.animate()
                    .scaleX(0f)
                    .scaleY(0f)
                    .duration = shortAnimTime
            }
    }

    override fun bindView(itemView: View) {
        view = itemView

        selectedCheckbox = itemView.findViewById(R.id.selected_checkbox)
        nameTV = itemView.findViewById(R.id.currency_full_name)
        codeTV = itemView.findViewById(R.id.currency_short_name)
        flagIV = itemView.findViewById(R.id.country_flag)
    }
}
