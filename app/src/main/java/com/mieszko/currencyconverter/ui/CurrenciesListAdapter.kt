package com.mieszko.currencyconverter.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.mieszko.currencyconverter.data.model.CurrencyListItemModel
import com.mieszko.currencyconverter.ui.viewholder.CurrencyViewHolder
import com.mieszko.currencyconverter.viewmodel.CurrenciesViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject


class CurrenciesListAdapter(private val viewModel: CurrenciesViewModel) : RecyclerView.Adapter<CurrencyViewHolder>() {

    private val listData = mutableListOf<CurrencyListItemModel>()
    val dataObservable: PublishSubject<List<CurrencyListItemModel>> = PublishSubject.create()

    init {
        val initialPair: Pair<List<CurrencyListItemModel>, DiffUtil.DiffResult?> = Pair(arrayListOf(), null)
        //todo unsubscribe from this
        dataObservable.scan(initialPair,
            { t1, oldItems ->
                val callback = CurrenciesDiffCallback(t1.first, oldItems)
                val calculatedDiff = DiffUtil.calculateDiff(callback)
                Pair(oldItems, calculatedDiff)
            })
            .skip(1)
            .subscribeOn(Schedulers.computation())
            .doOnNext { refreshList(it.first) }
            .map { it.second }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ diffResult -> diffResult?.dispatchUpdatesTo(this) }, {})
    }

    private fun refreshList(newResultsItems: List<CurrencyListItemModel>) {
        listData.clear()
        listData.addAll(newResultsItems)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        return CurrencyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        val currencyModel = listData[position]
        val isBase = position == 0
        val clickAction = if (isBase) {
            // base currency has no click effect
            {}
        } else {
            { viewModel.setBaseCurrency(currencyModel.currency) }
        }
        val valueChangeAction: ((Double) -> Unit) = if (isBase) {
            { newText -> viewModel.setBaseCurrencyAmount(newText) }
        } else {
            {}
        }

        holder.bind(currencyItem = currencyModel, isBaseCurrency = position == 0)
        holder.setClickAction(clickAction)
        holder.setValueChangeAction(valueChangeAction, isBase)
    }

    override fun onBindViewHolder(
        holder: CurrencyViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val currency = listData[position]
        val isBase = position == 0
        val clickAction = if (isBase) {
            // base currency has no click effect
            {}
        } else {
            { viewModel.setBaseCurrency(currency.currency) }
        }
        val valueChangeAction: ((Double) -> Unit) = if (isBase) {
            { newText -> viewModel.setBaseCurrencyAmount(newText) }
        } else {
            {}
        }

        if (payloads.isNotEmpty()) {
            //TODO STEP AWAY FROM HAVING "SETITEMTYPE" METHOD, AND CALL CONFIG METHODS DIRECTLY FROM HERE?
            holder.setItemType(position == 0)

            (payloads.first() as CurrencyChangePayload)
                .run {
                    // don't update value of base currency
                    if (position != 0) {
                        amount?.let { holder.setAmount(it) }
                        thisToBaseText?.let { holder.setThisToBaseText(it) }
                        baseToThisText?.let { holder.setBaseToThisText(it) }
                    }
                }
            //todo this might be overhead, as i set it again always even for same items
            holder.setClickAction(clickAction)
            holder.setValueChangeAction(valueChangeAction, isBase)
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
                if ((oldObj.amount != newObj.amount)) {
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
    }

    // null represents NO CHANGE state
    data class CurrencyChangePayload(
        var amount: Double? = null,
        var thisToBaseText: String? = null,
        var baseToThisText: String? = null
    )
}
