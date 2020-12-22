package com.mieszko.currencyconverter.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mieszko.currencyconverter.viewmodel.CurrenciesViewModel
import com.mieszko.currencyconverter.data.model.CurrencyListItemModel
import com.mieszko.currencyconverter.ui.viewholder.CurrencyViewHolder

class CurrenciesAdapter(
    private val viewModel: CurrenciesViewModel
) :
    RecyclerView.Adapter<CurrencyViewHolder>() {

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
        holder.bind(listData[position])
    }

    override fun onBindViewHolder(
        holder: CurrencyViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            (payloads.firstOrNull() as Double?)
                ?.run {
                    holder.updateAmount(this)
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
            return oldList[oldItemPosition].currency == newList[newItemPosition].currency
        }

        override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
            return oldList[oldPosition].amount == newList[newPosition].amount
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            val oldObj = oldList[oldItemPosition]
            val newObj = newList[newItemPosition]
            return if (newObj.amount != oldObj.amount) {
                newObj.amount
            } else
                null
        }
    }
}
