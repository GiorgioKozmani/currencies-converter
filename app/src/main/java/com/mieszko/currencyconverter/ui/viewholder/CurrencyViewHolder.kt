package com.mieszko.currencyconverter.ui.viewholder

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.common.SupportedCurrency
import com.mieszko.currencyconverter.data.model.CurrencyListItemModel
import com.mieszko.currencyconverter.viewmodel.CurrenciesViewModel
import de.hdodenhof.circleimageview.CircleImageView

class CurrencyViewHolder private constructor(
    itemView: View,
    private val viewModel: CurrenciesViewModel
) : RecyclerView.ViewHolder(itemView) {

    private val textSizeUnit = TypedValue.COMPLEX_UNIT_PX
    private val baseCurrencyTextSize =
        itemView.context.resources.getDimension(R.dimen.base_currency_name_text_size)
    private val regularCurrencyTextSize =
        itemView.context.resources.getDimension(R.dimen.regular_currency_name_text_size)

    private val rootContainer = itemView.findViewById<ViewGroup>(R.id.currency_item_root_container)
    private val amountET = itemView.findViewById<EditText>(R.id.currency_amount)
    private val fullNameTV = itemView.findViewById<TextView>(R.id.currency_full_name)
    private val baseToThisTV = itemView.findViewById<TextView>(R.id.base_to_this)
    private val thisToBaseTV = itemView.findViewById<TextView>(R.id.this_to_base)
    private val flagIV = itemView.findViewById<CircleImageView>(R.id.country_flag)

    //TODO DECOUPLE FOCUS FROM BASE / REGULAR

    fun bind(currencyItem: CurrencyListItemModel, isBaseCurrency: Boolean) {
        setupFocusChangeListener(itemView)

        setItemType(isBaseCurrency)

        with(currencyItem) {
            setFullNameText(this.currency.fullName)
            setAmountText(this.amount.toString())
            setThisToBaseText(this.thisToBase)
            setBaseToThisText(this.baseToThis)
            loadCurrencyFlag(this.currency.flagUrl)
            setupItemClickListener(this.currency)
        }
    }

    fun setItemType(isBaseCurrency: Boolean) {
        if (isBaseCurrency) {
            //todo fade in, don't just set color
            //todo gradient?
            rootContainer.background =
                ColorDrawable(ContextCompat.getColor(itemView.context, R.color.colorAccent))
            baseToThisTV.visibility = View.GONE
            thisToBaseTV.visibility = View.GONE
            fullNameTV.setTextSize(textSizeUnit, baseCurrencyTextSize)

        } else {
            rootContainer.background =
                ColorDrawable(ContextCompat.getColor(itemView.context, android.R.color.transparent))
            fullNameTV.setTextSize(textSizeUnit, regularCurrencyTextSize)
            baseToThisTV.visibility = View.VISIBLE
            thisToBaseTV.visibility = View.VISIBLE
        }
    }

    private fun setFullNameText(@StringRes currencyFullNameRes: Int) {
        fullNameTV.text = itemView.context.getString(currencyFullNameRes)
    }

    private fun setAmountText(amountText: String) {
        amountET.setText(amountText)
    }

    fun updateAmount(amountText: String) {
        // don't update base currency
        if (!amountET.isFocused) {
            amountET.setText(amountText)
        }
    }

    fun setThisToBaseText(newThisToBase: String) {
//        thisToBaseTV.fadeInText(newThisToBase)
        thisToBaseTV.text = newThisToBase
    }

    fun setBaseToThisText(newBaseToThis: String) {
//        baseToThisTV.fadeInText(newBaseToThis)
        baseToThisTV.text = newBaseToThis
    }

    private fun setupItemClickListener(currency: SupportedCurrency) {
        itemView.setOnClickListener {
            with(amountET) {
                requestFocus()
                setSelection(amountET.text.length)
            }
            viewModel.setBaseCurrency(currency)
        }
    }

    private fun loadCurrencyFlag(flagUrl: String) {
        Glide
            .with(itemView)
            .load(flagUrl)
            .apply(RequestOptions().apply { centerCrop() })
            .into(flagIV)
    }

    private fun View.showKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(this, InputMethodManager.SHOW_FORCED)
    }

    private fun String.toValidDouble(): Double {
        if (this.isBlank())
            return 0.0
        if (this.endsWith("."))
            return this.dropLast(1).toDouble()
        return this.toDouble()
    }

    private fun setupFocusChangeListener(itemView: View) {
        //There is a requirement that item with focused field has to be considered BaseCurrency
        amountET.run {
            val textWatcher = object : TextWatcher {
                var oldText = ""

                override fun afterTextChanged(s: Editable?) {
                    if (s.toString().startsWith(".")) {
                        setText("0".plus(s.toString()))
                        setSelection(2)
                        return
                    }

                    if (s.toString() != oldText) {
                        oldText = s.toString()
                        //todo this shouldn't be happening here. VH is too smart now
                        viewModel.setBaseCurrencyAmount(s.toString().toValidDouble())
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                }
            }

            setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    view.showKeyboard()
                    this.addTextChangedListener(textWatcher)
                    itemView.performClick()
                } else
                    this.removeTextChangedListener(textWatcher)
            }
        }
    }

    companion object {
        fun from(
            parent: ViewGroup,
            viewModel: CurrenciesViewModel
        ): CurrencyViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val itemView: View =
                layoutInflater.inflate(R.layout.list_item_base_currency, parent, false)
            return CurrencyViewHolder(itemView, viewModel)
        }
    }
}