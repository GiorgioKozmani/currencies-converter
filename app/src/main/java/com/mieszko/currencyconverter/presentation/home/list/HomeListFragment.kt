package com.mieszko.currencyconverter.presentation.home.list

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.common.model.Resource
import com.mieszko.currencyconverter.domain.model.list.HomeListModel
import com.mieszko.currencyconverter.presentation.home.HomeViewModel
import com.mieszko.currencyconverter.presentation.home.list.adapter.HomeCurrenciesListAdapter
import com.mieszko.currencyconverter.presentation.util.CurrenciesListDragHelper
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class HomeListFragment : Fragment(R.layout.home_list_fragment) {
    private val viewModel by sharedViewModel<HomeViewModel>()

    private lateinit var recyclerView: RecyclerView
    private val rvAdapter: HomeCurrenciesListAdapter by lazy { HomeCurrenciesListAdapter(viewModel) }
    private val rvDragHelper: CurrenciesListDragHelper by lazy {
        CurrenciesListDragHelper { from, to -> viewModel.itemDragged(from, to) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView(view)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.getCurrenciesLiveData()
            .observe(viewLifecycleOwner, { handleNewResults(it) })
    }

    private fun setupRecyclerView(view: View) {
        recyclerView = view.findViewById<RecyclerView>(R.id.currencies_rv).apply {
            setItemViewCacheSize(30)
            adapter = rvAdapter
        }

        rvDragHelper.attachToRecyclerView(recyclerView)
        rvAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                if (!rvDragHelper.isUserDraggingItem() || toPosition == 0) {
                    recyclerView.postOnAnimation { recyclerView.scrollToPosition(0) }
                }
            }
        })
    }

    private fun handleNewResults(response: Resource<List<HomeListModel>>) {
        when (response) {
            is Resource.Loading -> {
                // loading is not handled yet
            }
            is Resource.Success -> {
                // TODO IT'S called 3 times!!! ON START AND 2 LATER ON!
                // todo important note. Once we get bug with multiple bases data we send to adapter still has only 1 base!
                updateListData(response)
            }
            is Resource.Error -> {
                Toast.makeText(context, "TODO NETWORK REFRESH ERROR", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateListData(response: Resource<List<HomeListModel>>) {
        val rvManager = recyclerView.layoutManager as LinearLayoutManager
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
