package com.mieszko.currencyconverter.presentation.home.list.adapter

import android.annotation.SuppressLint
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.common.util.hideKeyboard
import com.mieszko.currencyconverter.common.util.showKeyboard
import com.mieszko.currencyconverter.domain.model.list.HomeListModel
import de.hdodenhof.circleimageview.CircleImageView
import java.text.DecimalFormat


// TODO THINGS NEED TO BE SIMPLIFIED HERE, IT TAKES WAY TOO LONG TIME TO BIND THIS VIEWH HOLDER, AND FIRST SCROLLING IS SLOW
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

    private var baseTextWatcher: TextWatcher? = null
    private var baseTextTextChangeAction: ((Double) -> Unit)? = null

    // TODO HAVE THIS TEXTWATCHER BEING INIT ONLY ONCE AND SHARED ACROSS VHs
    //TODO DECOUPLE FOCUS FROM BASE / REGULAR
    fun bind(
        currencyModel: HomeListModel,
        baseValueChangeAction: ((Double) -> Unit),
        clickAction: () -> Unit
    ) {
        itemView.setOnClickListener { clickAction() }
        baseTextTextChangeAction = baseValueChangeAction

        with(currencyModel) {
            setNameText(this.codeData.name)
            setAmount(this.amount)
            loadCurrencyFlag(this.codeData.flagResId)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setupBaseItem() {
        amountET.removeTextChangedListener(baseTextWatcher)
        baseTextWatcher = amountET.doAfterTextChanged {
            baseTextTextChangeAction?.let { changeAction ->
                changeAction(it.toString().sanitizeCurrencyValue().toDouble())
            }
        }

            amountET.isFocusableInTouchMode = true
            amountET.setTextIsSelectable(true)

        amountET.post {
            amountET.requestFocus()
            amountET.showKeyboard()
        }

        amountET.setOnTouchListener { _, _ -> false }
        setBaseUI()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setupNonBaseItem(currencyModel: HomeListModel.NonBase) {
        amountET.removeTextChangedListener(baseTextWatcher)

        amountET.setOnTouchListener { _, event ->
            // make parent duplicate touch events so it can be dragged by ET
            itemView.onTouchEvent(event)
            // do not consume event
            false
        }

        amountET.isFocusableInTouchMode = false
        amountET.setTextIsSelectable(false)
        amountET.clearFocus()

        setThisToBaseText(currencyModel.thisToBaseText)
        setBaseToThisText(currencyModel.baseToThisText)

        setNonBaseUI()
    }

    private fun setBaseUI() {
        //todo gradient?
        baseToThisTV.visibility = View.GONE
        thisToBaseTV.visibility = View.GONE
        nameTV.setTextSize(textSizeUnit, baseCurrencyTextSize)
        baseBackgroundOverlay.animate()
            .alpha(1f)
            .setDuration(BASE_OVERLAY_TRANSITION_DURATION)
            .setListener(null)
    }

    private fun setNonBaseUI() {
        nameTV.setTextSize(textSizeUnit, regularCurrencyTextSize)
        baseToThisTV.visibility = View.VISIBLE
        thisToBaseTV.visibility = View.VISIBLE
        baseBackgroundOverlay.animate()
            .alpha(0f)
            .setDuration(BASE_OVERLAY_TRANSITION_DURATION)
            .setListener(null)
    }

    fun removeBaseOverlayInstantly() {
        baseBackgroundOverlay.alpha = 0f
    }

    private fun setNameText(currencyName: String) {
        nameTV.text = currencyName
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
        thisToBaseTV.text = newThisToBase
    }

    fun setBaseToThisText(newBaseToThis: String) {
        baseToThisTV.text = newBaseToThis
    }

    private fun loadCurrencyFlag(@DrawableRes flagRes: Int) {
        Glide.with(itemView)
            .load(flagRes)
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

    fun clearFocusAndHideKeyboard() {
        amountET.clearFocus()
        amountET.hideKeyboard()
    }

    companion object {
        private const val BASE_OVERLAY_TRANSITION_DURATION: Long = 250

        fun from(parent: ViewGroup): HomeCurrencyViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val itemView: View =
                layoutInflater.inflate(R.layout.home_list_item, parent, false)
            return HomeCurrencyViewHolder(itemView)
        }
    }
}