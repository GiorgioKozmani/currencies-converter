package com.mieszko.currencyconverter.ui

import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.data.model.CurrencyListItemModel
import com.mieszko.currencyconverter.data.model.Resource
import com.mieszko.currencyconverter.viewmodel.CurrenciesViewModel
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class MainActivity : AppCompatActivity() {

    private var rvAdapter: CurrenciesListAdapter? = null
    private val recyclerView: RecyclerView by lazy { findViewById(R.id.currencies_rv) }
    private val rvManager: LinearLayoutManager by lazy { LinearLayoutManager(this) }
    private val viewModel by viewModel<CurrenciesViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupRecyclerView()
        observeViewModel()
    }

    private fun observeViewModel() {
        with(viewModel) {
            getCurrenciesLiveDate()
                .observe(this@MainActivity, {
                    handleNewResults(it)
                })

            getLastUpdatedLiveData()
                .observe(this@MainActivity, {
                    handleNewDate(it)
                })
        }
    }

    private fun setupRecyclerView() {
        val adapter = CurrenciesListAdapter(viewModel)
        rvAdapter = adapter
        recyclerView.layoutManager = rvManager
        recyclerView.adapter = adapter
        itemTouchHelper.attachToRecyclerView(recyclerView)

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeMoved(
                fromPosition: Int,
                toPosition: Int,
                itemCount: Int
            ) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                if (!isUserDraggingItem || toPosition == 0) {
                    recyclerView.post {
                        recyclerView.scrollToPosition(0)
                    }
                }
            }
        })
    }

    private fun handleNewResults(response: Resource<List<CurrencyListItemModel>>) {
        when (response) {
            is Resource.Loading -> {

            }
            is Resource.Success -> {
                updateListData(response)
                hideErrorMessage()
            }
            is Resource.Error -> {
                showErrorMessage(response)
            }
        }
    }

    //todo step away from synthetics, go for viewbinding
    private fun updateListData(response: Resource<List<CurrencyListItemModel>>) {
        // this is a workaround for issue with dragging 1st item of the list
        // https://issuetracker.google.com/issues/37018279
        val firstPos = rvManager.findFirstCompletelyVisibleItemPosition()
        var offsetTop = 0
        if (firstPos >= 0) {
            val firstView = rvManager.findViewByPosition(firstPos)!!
            offsetTop =
                rvManager.getDecoratedTop(firstView) - rvManager.getTopDecorationHeight(firstView)
        }

        rvAdapter?.updateCurrencies(response.data ?: listOf())

        if (firstPos >= 0) {
            rvManager.scrollToPositionWithOffset(firstPos, offsetTop)
        }
    }

    private fun hideErrorMessage() {
        error_message.visibility = View.GONE
    }

    private fun showErrorMessage(response: Resource<List<CurrencyListItemModel>>) {
        error_message.text = response.message
        error_message.visibility = View.VISIBLE
    }

    private fun handleNewDate(updateDate: Resource<Date>) {
        when (updateDate) {
            is Resource.Loading -> {

            }
            is Resource.Success -> {
                //todo if today / yesterday then think of using these strings instead of full day
                last_updated.text = DateFormat.format("yyyy-MM-dd hh:mm:ss a", updateDate.data)
            }
            is Resource.Error -> {
            }
        }
    }

    var isUserDraggingItem = false

    private val itemTouchHelper by lazy {
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(UP or DOWN or START or END, 0) {

                override fun onSelectedChanged(
                    viewHolder: RecyclerView.ViewHolder?,
                    actionState: Int
                ) {
                    when (actionState) {
                        ACTION_STATE_DRAG -> {
                            isUserDraggingItem = true
                            viewHolder?.itemView?.alpha = 0.5f
                        }
                    }
                    super.onSelectedChanged(viewHolder, actionState)
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    viewHolder.itemView.alpha = 1.0f
                    isUserDraggingItem = false
                    super.clearView(recyclerView, viewHolder)
                }

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {

                    val from = viewHolder.adapterPosition
                    val to = target.adapterPosition

                    viewModel.moveItem(from, to)

                    return true
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                    //    ItemTouchHelper handles horizontal swipe as well, but
                    //    it is not relevant with reordering. Ignoring here.
                }
            }
        ItemTouchHelper(simpleItemTouchCallback)
    }
}
