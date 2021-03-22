package com.mieszko.currencyconverter.presentation.home.list.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mieszko.currencyconverter.domain.model.list.HomeListModel
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
        holder.removeBaseOverlayInstantly()
    }

    override fun onBindViewHolder(holder: HomeCurrencyViewHolder, position: Int) {
        val currencyModel = currentListData[position]

        // TODO THIS IS TO BE CHANGED AS CURRENTLY I HAVE TO CALL BIND BEFORE SETUP FOR IT TO WORK. THIS IS
        // NOT RIGHT, AS IT'S EASY TO FORGET
        holder.bind(
            currencyModel = currencyModel,
            baseValueChangeAction = { newText -> viewModel.baseCurrencyAmountChanged(newText) },
            clickAction = { viewModel.listItemClicked(currencyModel.code) })
        // TODO MERGE INTO ONE
        when (currencyModel) {
            is HomeListModel.Base -> {
                //TODO CLICK ACTION SHOULD BE ONLY LETTING VM KNOW WHAT ITEM GOT CLICKED! IT'LL DECIDE WHETHER TO MAKE IT
                // BASE OR IGNORE
                holder.setupBaseItem()
            }
            is HomeListModel.NonBase -> {
                holder.setupNonBaseItem(currencyModel)
            }
        }
    }

    override fun onBindViewHolder(
        holder: HomeCurrencyViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val currency = currentListData[position]

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
                    holder.setupBaseItem()
                }
                is ChangedToNonBase -> {
                    //todo rethink if it's not better to set it once as lambda
                    // don't update value of base currency
                    holder.setupNonBaseItem(currency as HomeListModel.NonBase)
                }
            }
        } else super.onBindViewHolder(holder, position, payloads)
    }

    override fun onViewDetachedFromWindow(holder: HomeCurrencyViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.run {
            if (adapterPosition == 0) {
                clearFocusAndHideKeyboard()
            }
        }
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

        // calls onBindViewHolder with no payload
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            // items represent the same currency
            return oldList[oldItemPosition].code == newList[newItemPosition].code
        }

        // calls onBindViewHolder with payload
        override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
            return oldList[oldPosition] == newList[newPosition]
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any {
            val oldObj = oldList[oldItemPosition]
            val newObj = newList[newItemPosition]

            if (newObj is HomeListModel.Base && oldObj is HomeListModel.Base) {
                // do nothing if body changed as it is most probably amount change
            }

            if (newObj is HomeListModel.NonBase && oldObj is HomeListModel.NonBase) {
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
                is HomeListModel.NonBase -> ChangedToNonBase(
                    newObj.thisToBaseText,
                    newObj.baseToThisText
                )
            }
        }
    }

    object ChangedToBase

    data class ChangedToNonBase(var thisToBaseText: String, var baseToThisText: String)

    // null represents NO CHANGE state
    data class NonBaseCurrencyChange(
        var amount: Double? = null,
        var thisToBaseText: String? = null,
        var baseToThisText: String? = null
    )
}
