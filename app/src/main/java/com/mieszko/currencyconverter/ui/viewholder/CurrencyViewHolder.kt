package com.mieszko.currencyconverter.ui.viewholder

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mieszko.currencyconverter.viewmodel.CurrenciesViewModel
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.data.model.CurrencyListItemModel

class CurrencyViewHolder private constructor(
    itemView: View,
    private val viewModel: CurrenciesViewModel
) :
    RecyclerView.ViewHolder(itemView) {

    private val amountET = itemView.findViewById<EditText>(R.id.currency_amount)

    fun bind(currencyItem: CurrencyListItemModel) {
        setupFocusChangeListener(itemView)

        with(currencyItem){
            setShortNameText(this)
            setFullNameText(this)
            setAmountText(this)
            loadCurrencyFlag(this)
            setupItemClickListener(this)
        }
    }

    fun updateAmount(newAmount: Double) {
        if (!amountET.isFocused)
            amountET.setText(newAmount.toString())
    }


    private fun setupItemClickListener(currencyItem: CurrencyListItemModel) {
        itemView.setOnClickListener {
            with(amountET) {
                requestFocus()
                setSelection(amountET.text.length)
            }
            viewModel.setBaseCurrency(currencyItem.currency)
        }
    }

    private fun loadCurrencyFlag(currencyItem: CurrencyListItemModel) {
        Glide
            .with(itemView)
            .load(currencyItem.currency.flagUrl)
            .apply(RequestOptions().apply { centerCrop() })
            .into(itemView.findViewById(R.id.country_flag))
    }

    private fun setAmountText(currencyItem: CurrencyListItemModel) {
        amountET.setText(currencyItem.amount.toString())
    }

    private fun setFullNameText(currencyItem: CurrencyListItemModel) {
        itemView.findViewById<TextView>(R.id.currency_full_name).text =
            itemView.context.getString(currencyItem.currency.fullName)
    }

    private fun setShortNameText(currencyItem: CurrencyListItemModel) {
        itemView.findViewById<TextView>(R.id.currency_short_name).text =
            currencyItem.currency.name
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