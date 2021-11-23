package com.mieszko.currencyconverter.presentation.home.list.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mieszko.currencyconverter.domain.model.list.HomeListItemModel
import com.mieszko.currencyconverter.presentation.home.HomeViewModel

// TODO WHEN 52. (WITH NO DECIMALS AND DOT IN THE END) GETS MOVED DOWN
//  , RESET TO 52.00
class HomeCurrenciesListAdapter(private val viewModel: HomeViewModel) :
    RecyclerView.Adapter<HomeCurrencyViewHolder>() {

    private val currentListData = mutableListOf<HomeListItemModel>()

    fun updateCurrencies(currencies: List<HomeListItemModel>) {
        DiffUtil.calculateDiff(CurrenciesDiffCallback(currentListData, currencies))
            .dispatchUpdatesTo(this)
        refreshList(currencies)
    }

    private fun refreshList(newResultsModels: List<HomeListItemModel>) {
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
            clickAction = { viewModel.listItemClicked(currencyModel.code) }
        )

        // TODO MERGE INTO ONE
        when (currencyModel) {
            is HomeListItemModel.Base -> {
                // TODO CLICK ACTION SHOULD BE ONLY LETTING VM KNOW WHAT ITEM GOT CLICKED! IT'LL DECIDE WHETHER TO MAKE IT
                // BASE OR IGNORE
                holder.setupBaseItem()
            }
            is HomeListItemModel.NonBase -> {
                holder.setupNonBaseItem(currencyModel)
            }
        }
    }

    override fun onBindViewHolder(
        holder: HomeCurrencyViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            val modelChange = payloads.last() as ModelChange
            val newObj = modelChange.new
            val oldObj = modelChange.old

            if (newObj is HomeListItemModel.Base && oldObj is HomeListItemModel.Base) {
                // do nothing, this will be most probably base value change, which shouldn't trigger the update just so we don't mess with cursor
                return
            }

            if (newObj is HomeListItemModel.NonBase && oldObj is HomeListItemModel.NonBase) {
                if (oldObj.amount != newObj.amount) {
                    holder.setAmount(newObj.amount)
                }
                if (oldObj.thisToBaseText != newObj.thisToBaseText) {
                    holder.setThisToBaseText(newObj.thisToBaseText)
                }
                if (oldObj.baseToThisText != newObj.baseToThisText) {
                    holder.setBaseToThisText(newObj.baseToThisText)
                }

                return
            }

            when (newObj) {
                is HomeListItemModel.Base -> {
                    holder.setupBaseItem()
                    // This is only done in this overload of onBindViewHolder, because it won't be triggered on screen opening.
                    // We don't want the keyboard to show up automatically after navigating to calculator.
                    holder.requestAmountFocus()
                }
                is HomeListItemModel.NonBase -> holder.setupNonBaseItem(newObj)
            }
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun onViewDetachedFromWindow(holder: HomeCurrencyViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.run {
            if (absoluteAdapterPosition == 0) {
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
        private val oldList: List<HomeListItemModel>,
        private val newList: List<HomeListItemModel>
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

            return ModelChange(oldObj, newObj)
        }
    }

    data class ModelChange(val old: HomeListItemModel, val new: HomeListItemModel)
}
