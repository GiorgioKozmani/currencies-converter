package com.mieszko.currencyconverter.presentation.home.list.adapter

import android.annotation.SuppressLint
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.domain.model.HomeListModel
import de.hdodenhof.circleimageview.CircleImageView
import java.text.DecimalFormat


class HomeCurrencyViewHolder private constructor(itemView: View) :
    RecyclerView.ViewHolder(itemView) {
    //todo bug, now some random keboard is showing when scrolling

    private val baseBackgroundOverlay = itemView.findViewById<View>(R.id.base_background_overlay)
    private val amountET = itemView.findViewById<EditText>(R.id.currency_amount)
    private val nameTV = itemView.findViewById<TextView>(R.id.currency_full_name)
    private val baseToThisTV = itemView.findViewById<TextView>(R.id.base_to_this)
    private val thisToBaseTV = itemView.findViewById<TextView>(R.id.this_to_base)
    private val flagIV = itemView.findViewById<CircleImageView>(R.id.country_flag)

    private val textSizeUnit = TypedValue.COMPLEX_UNIT_PX
    private val baseCurrencyTextSize =
        itemView.context.resources.getDimension(R.dimen.base_currency_name_text_size)
    private val regularCurrencyTextSize =
        itemView.context.resources.getDimension(R.dimen.regular_currency_name_text_size)

    private var textWatcher: TextWatcher? = null

    //TODO DECOUPLE FOCUS FROM BASE / REGULAR
    fun bind(currencyModel: HomeListModel) {
        //todo think of 2 separate methods bind base bind regular
        amountET.removeTextChangedListener(textWatcher)

        when (currencyModel) {
            is HomeListModel.Base -> {
                isBase(true)

                //todo hide ttb and btt
            }
            is HomeListModel.Regular -> {
                isBase(false)

                setThisToBaseText(currencyModel.thisToBaseText)
                setBaseToThisText(currencyModel.baseToThisText)
            }
        }


        with(currencyModel) {
            setNameText(this.codeData.name)
            setAmount(this.amount)
            loadCurrencyFlag(this.codeData.flagUrl)
        }
    }

    fun setClickAction(clickAction: () -> Unit) {
        //todo factor out sanitizing
        itemView.setOnClickListener { clickAction.invoke() }
    }

    //todo rethink, why to reset it if i could remove from adapter
    fun setValueChangeAction(afterTextChangeAction: ((Double) -> Unit), isBaseCurrency: Boolean) {
        amountET.removeTextChangedListener(textWatcher)

        if (isBaseCurrency) {
            textWatcher = amountET.doAfterTextChanged {
                afterTextChangeAction(it.toString().sanitizeCurrencyValue().toDouble())
            }
        }
    }

    fun resetBaseOverlayImmediately(){
        baseBackgroundOverlay.alpha = 0f
    }

    @SuppressLint("ClickableViewAccessibility")
    fun isBase(isBaseCurrency: Boolean) {
        if (isBaseCurrency) {
            amountET.setOnTouchListener { _, _ ->
                false
            }

            amountET.isFocusableInTouchMode = true
            amountET.requestFocusFromTouch()

            //todo fade in, don't just set color
            //todo gradient?


            baseBackgroundOverlay.animate().alpha(1f).setDuration(BASE_OVERLAY_TRANSITION_DURATION).setListener(null)

            baseToThisTV.visibility = View.GONE
            thisToBaseTV.visibility = View.GONE
            nameTV.setTextSize(textSizeUnit, baseCurrencyTextSize)
        } else {
            amountET.setOnTouchListener { v, event ->
                itemView.onTouchEvent(event)
                // do not consume event
                false
            }
            amountET.isFocusableInTouchMode = false
            amountET.setTextIsSelectable(false)
            amountET.clearFocus()


            baseBackgroundOverlay.animate().alpha(0f).setDuration(BASE_OVERLAY_TRANSITION_DURATION).setListener(null)


            nameTV.setTextSize(textSizeUnit, regularCurrencyTextSize)
            baseToThisTV.visibility = View.VISIBLE
            thisToBaseTV.visibility = View.VISIBLE
        }
    }

    private fun setNameText(@StringRes currencyNameRes: Int) {
        nameTV.text = itemView.context.getString(currencyNameRes)
    }

    fun setAmount(newAmount: Double) {
        val currentAmountText = amountET.text.toString()
        if (currentAmountText.isBlank() || currentAmountText != "." || newAmount != currentAmountText.toDouble()) {
            amountET.setText(formatValue(newAmount).sanitizeCurrencyValue())
        }
    }

    //todo rename, this can be lifted up to have format only init once
    private fun formatValue(value: Double): String {
        // Here you can also deal with rounding if you wish..
        return DecimalFormat("0.00").format(value)
    }

    fun setThisToBaseText(newThisToBase: String) {
        //todo think if this effect is desirable
//        thisToBaseTV.fadeInText(newThisToBase)
        thisToBaseTV.text = newThisToBase
    }

    fun setBaseToThisText(newBaseToThis: String) {
        baseToThisTV.text = newBaseToThis
    }

    private fun loadCurrencyFlag(flagUrl: String) {
        Glide.with(itemView)
            .load(flagUrl)
            .apply(RequestOptions().apply { centerCrop() })
            .into(flagIV)
    }

    private fun String.sanitizeCurrencyValue(): String {
        if (this.trim() == "") {
            return "0"
        }

        if (this.trim() == ".") {
            return "0."
        }

        var strToReturn = this
        if (strToReturn.startsWith(".")) {
            strToReturn = "0$strToReturn"
        }

        val hasDecimal = strToReturn.contains('.')
        var whole: String
        var fraction: String

        if (hasDecimal) {
            strToReturn.split('.').let {
                whole = it.first()
                fraction = it.last()
            }

            // > 2 as we know that there is one "." and at least one "0"
            if (whole.startsWith("0") && whole.length > 2) {
                whole = whole.trimStart { it == '0' }.let { trimmedFromZeros ->
                    if (trimmedFromZeros.isEmpty()) {
                        "0"
                    } else trimmedFromZeros
                }
            }

            fraction = fraction.take(2)

            strToReturn = "$whole.$fraction"
        } else {
            // this clears front zeros, if all zeros are cleared, replace with "0"
            if (strToReturn.startsWith("0") && strToReturn.length > 1) {
                strToReturn = strToReturn.trimStart { it == '0' }.let { trimmedFromZeros ->
                    if (trimmedFromZeros.isEmpty()) {
                        "0"
                    } else trimmedFromZeros
                }
            }
        }

        return strToReturn
    }

    companion object {
        private const val BASE_OVERLAY_TRANSITION_DURATION: Long = 250

        fun from(parent: ViewGroup): HomeCurrencyViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val itemView: View =
                layoutInflater.inflate(R.layout.home_currency_list_item, parent, false)
            return HomeCurrencyViewHolder(itemView)
        }
    }
}