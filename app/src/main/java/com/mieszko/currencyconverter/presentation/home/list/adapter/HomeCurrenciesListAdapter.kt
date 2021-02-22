package com.mieszko.currencyconverter.presentation.home.list.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mieszko.currencyconverter.domain.model.HomeListModel
import com.mieszko.currencyconverter.presentation.home.HomeViewModel

class HomeCurrenciesListAdapter(private val viewModel: HomeViewModel) :
    RecyclerView.Adapter<HomeCurrencyViewHolder>() {

    private val currentListData = mutableListOf<HomeListModel>()

    fun updateCurrencies(currencies: List<HomeListModel>) {
        DiffUtil.calculateDiff(CurrenciesDiffCallback(currentListData, currencies))
            .dispatchUpdatesTo(this)
        refreshList(currencies)
    }

    private fun refreshList(newResultsModels: List<HomeListModel>) {
        currentListData.clear()
        currentListData.addAll(newResultsModels)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCurrencyViewHolder {
        return HomeCurrencyViewHolder.from(parent)
    }

    override fun onViewRecycled(holder: HomeCurrencyViewHolder) {
        super.onViewRecycled(holder)
        holder.resetBaseOverlayImmediately()
    }

    override fun onBindViewHolder(holder: HomeCurrencyViewHolder, position: Int) {
        val currencyModel = currentListData[position]
        val isBase = position == 0
        val clickAction = if (isBase) {
            // base currency has no click effect
            {}
        } else {
            { viewModel.setBaseCurrency(currencyModel.code) }
        }
        val valueChangeAction: ((Double) -> Unit) = if (isBase) {
            { newText -> viewModel.setBaseCurrencyAmount(newText) }
        } else {
            {}
        }

        holder.bind(currencyModel = currencyModel)
        holder.setClickAction(clickAction)
        holder.setValueChangeAction(valueChangeAction, isBase)
    }

    override fun onBindViewHolder(
        holder: HomeCurrencyViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val currency = currentListData[position]
        val isBase = position == 0
        val clickAction = if (isBase) {
            // base currency has no click effect
            {}
        } else {
            { viewModel.setBaseCurrency(currency.code) }
        }
        val valueChangeAction: ((Double) -> Unit) = if (isBase) {
            { newText -> viewModel.setBaseCurrencyAmount(newText) }
        } else {
            {}
        }

        if (payloads.isNotEmpty()) {
            //TODO STEP AWAY FROM HAVING "SETITEMTYPE" METHOD, AND CALL CONFIG METHODS DIRECTLY FROM HERE?
            when (val lastPayload = payloads.last()) {
                is NonBaseCurrencyChange -> {
                    lastPayload.run {
                        // don't update value of base currency
                        if (position != 0) {
                            amount?.let { holder.setAmount(it) }
                            thisToBaseText?.let { holder.setThisToBaseText(it) }
                            baseToThisText?.let { holder.setBaseToThisText(it) }
                        }
                    }
                }
                is ChangedToBase -> {
                    // don't update value of base currency
                    holder.isBase(true)
                }
                is ChangedToNonBase -> {
                    // don't update value of base currency
                    holder.isBase(false)
                    holder.setThisToBaseText(lastPayload.thisToBaseText)
                    holder.setBaseToThisText(lastPayload.baseToThisText)
                }
            }

            //todo this might be overkill, as i set it again always even for same items
            holder.setClickAction(clickAction)
            holder.setValueChangeAction(valueChangeAction, isBase)
        } else
            super.onBindViewHolder(holder, position, payloads)
    }

    override fun getItemId(position: Int): Long {
        return currentListData[position].code.ordinal.toLong()
    }

    override fun getItemCount(): Int {
        return currentListData.size
    }

    class CurrenciesDiffCallback(
        private val oldList: List<HomeListModel>,
        private val newList: List<HomeListModel>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            // items represent the same currency
            return oldList[oldItemPosition].code == newList[newItemPosition].code
        }

        override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
            return oldList[oldPosition] == newList[newPosition]
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any {
            val oldObj = oldList[oldItemPosition]
            val newObj = newList[newItemPosition]

            if (newObj is HomeListModel.Base && oldObj is HomeListModel.Base) {
                // do nothing if body changed as it is most probably amount change
            }

            if (newObj is HomeListModel.Regular && oldObj is HomeListModel.Regular) {
                return NonBaseCurrencyChange().apply {
                    if (oldObj.amount != newObj.amount) {
                        amount = newObj.amount
                    }
                    if (oldObj.thisToBaseText != newObj.thisToBaseText) {
                        thisToBaseText = newObj.thisToBaseText
                    }
                    if (oldObj.baseToThisText != newObj.baseToThisText) {
                        baseToThisText = newObj.baseToThisText
                    }
                }
            }

            return when (newObj) {
                is HomeListModel.Base -> ChangedToBase
                is HomeListModel.Regular -> ChangedToNonBase(
                    newObj.thisToBaseText,
                    newObj.baseToThisText
                )
            }
        }
    }

    object ChangedToBase

    //todo rethink if this shouldn't be replaced with NONBASECURRENCYCHANGE
    data class ChangedToNonBase(var thisToBaseText: String, var baseToThisText: String)

    // null represents NO CHANGE state
    data class NonBaseCurrencyChange(
        var amount: Double? = null,
        var thisToBaseText: String? = null,
        var baseToThisText: String? = null
    )
}
