package com.mieszko.currencyconverter.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mieszko.currencyconverter.data.model.CurrencyListItemModel
import com.mieszko.currencyconverter.ui.viewholder.CurrencyViewHolder
import com.mieszko.currencyconverter.viewmodel.CurrenciesViewModel

class CurrenciesAdapter(
    private val viewModel: CurrenciesViewModel
) : RecyclerView.Adapter<CurrencyViewHolder>() {

    private val listData = mutableListOf<CurrencyListItemModel>()

    fun refreshData(newItems: List<CurrencyListItemModel>) {
        getDiffResult(listData, newItems)
            .dispatchUpdatesTo(this)
        refreshList(newItems)
    }

    private fun refreshList(newResultsItems: List<CurrencyListItemModel>) {
        listData.clear()
        listData.addAll(newResultsItems)
    }

    private fun getDiffResult(
        oldItems: List<CurrencyListItemModel>,
        newItems: List<CurrencyListItemModel>
    ): DiffUtil.DiffResult {
        return DiffUtil.calculateDiff(CurrenciesDiffCallback(oldItems, newItems))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        return CurrencyViewHolder.from(parent, viewModel)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        holder.bind(listData[position], position == 0)
    }

    override fun onBindViewHolder(
        holder: CurrencyViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        //TODO FOR BASE CURRENCY:
        // - HIDE SUBTEXTS
        // - INCREASE TEXT SIZE
        if (payloads.isNotEmpty()) {
            holder.setItemType(position == 0)
            (payloads.first() as CurrencyChangePayload)
                .run {
                    amount?.let {
                        holder.updateAmount(it.toString())
                    }
                    thisToBaseText?.let {
                        holder.setThisToBaseText(it)
                    }
                    baseToThisText?.let {
                        holder.setBaseToThisText(it)
                    }
                }
        } else
            super.onBindViewHolder(holder, position, payloads)
    }

    override fun getItemId(position: Int): Long {
        return listData[position].currency.ordinal.toLong()
    }

    override fun getItemCount(): Int {
        return listData.size
    }

    class CurrenciesDiffCallback(
        private val oldList: List<CurrencyListItemModel>,
        private val newList: List<CurrencyListItemModel>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            // items represent the same currency
            return oldList[oldItemPosition].currency == newList[newItemPosition].currency
        }

        override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
            return oldList[oldPosition] == newList[newPosition]
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any {
            val oldObj = oldList[oldItemPosition]
            val newObj = newList[newItemPosition]

            return CurrencyChangePayload().apply {
                if (oldObj.amount != newObj.amount) {
                    amount = newObj.amount
                }
                if (oldObj.thisToBase != newObj.thisToBase) {
                    thisToBaseText = newObj.thisToBase
                }
                if (oldObj.baseToThis != newObj.baseToThis) {
                    baseToThisText = newObj.baseToThis
                }
            }
        }
    }

    // null represents NO CHANGE state
    data class CurrencyChangePayload(
        var amount: Double? = null,
        var thisToBaseText: String? = null,
        var baseToThisText: String? = null
    )
}
