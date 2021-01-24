package com.mieszko.currencyconverter.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.data.model.CurrencyListItemModel
import com.mieszko.currencyconverter.data.model.Resource
import com.mieszko.currencyconverter.ui.util.CurrenciesListDragHelper
import com.mieszko.currencyconverter.viewmodel.CurrenciesViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CurrenciesListFragment : Fragment(R.layout.currencies_list_fragment) {
    private val viewModel by sharedViewModel<CurrenciesViewModel>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var rvAdapter: CurrenciesListAdapter
    private lateinit var rvManager: LinearLayoutManager
    private lateinit var rvDragHelper: CurrenciesListDragHelper

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.currencies_rv)
        rvAdapter = CurrenciesListAdapter(viewModel)
        rvManager = LinearLayoutManager(view.context)
        rvDragHelper = CurrenciesListDragHelper { from, to -> viewModel.moveItem(from, to) }
        setupRecyclerView()
        observeViewModel()
    }

    private fun observeViewModel() {
            viewModel.getCurrenciesLiveDate()
                .observe(viewLifecycleOwner, { handleNewResults(it) })
    }

    private fun setupRecyclerView() {
        recyclerView.adapter = rvAdapter
        recyclerView.layoutManager = rvManager
        rvDragHelper.attachToRecyclerView(recyclerView)
        rvAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                if (!rvDragHelper.isUserDraggingItem() || toPosition == 0) {
                    recyclerView.post { recyclerView.scrollToPosition(0) }
                }
            }
        })
    }

    private fun handleNewResults(response: Resource<List<CurrencyListItemModel>>) {
        when (response) {
            is Resource.Loading -> {
                // loading is not handled yet
            }
            is Resource.Success -> {
                updateListData(response)
            }
            is Resource.Error -> {
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

        rvAdapter.updateCurrencies(response.data ?: listOf())

        if (firstPos >= 0) {
            rvManager.scrollToPositionWithOffset(firstPos, offsetTop)
        }
    }
}