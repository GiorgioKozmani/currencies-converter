package com.mieszko.currencyconverter.presentation.home.list

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.common.model.Resource
import com.mieszko.currencyconverter.domain.model.list.HomeListItemModel
import com.mieszko.currencyconverter.presentation.home.HomeViewModel
import com.mieszko.currencyconverter.presentation.home.list.adapter.HomeCurrenciesListAdapter
import com.mieszko.currencyconverter.presentation.util.CurrenciesListDragHelper
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class HomeListFragment : Fragment(R.layout.home_list_fragment) {
    private val viewModel by sharedViewModel<HomeViewModel>()

    private lateinit var recyclerView: RecyclerView
    private val rvAdapter: HomeCurrenciesListAdapter by lazy { HomeCurrenciesListAdapter(viewModel) }
    private lateinit var emptyState: View
    private val rvDragHelper: CurrenciesListDragHelper by lazy {
        CurrenciesListDragHelper { from, to -> viewModel.itemDragged(from, to) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        assignViews(view)
        setupRecyclerView(recyclerView)
        observeViewModel()
    }

    private fun assignViews(view: View) {
        emptyState = view.findViewById(R.id.no_content_state_holder)
        recyclerView = view.findViewById(R.id.currencies_rv)
    }

    private fun observeViewModel() {
        viewModel.getCurrenciesLiveData()
            .observe(viewLifecycleOwner, { handleNewResults(it) })

        viewModel.getShowEmptyStateLiveData()
            .observe(
                viewLifecycleOwner,
                { showEmptyState ->
                    if (showEmptyState) {
                        showEmptyState()
                    } else {
                        hideEmptyState()
                    }
                }
            )
    }

    private fun showEmptyState() {
        emptyState.visibility = View.VISIBLE
    }

    private fun hideEmptyState() {
        emptyState.visibility = View.GONE
    }

    private fun setupRecyclerView(rv: RecyclerView) {
        rv.setItemViewCacheSize(30)
        rv.adapter = rvAdapter
        rvDragHelper.attachToRecyclerView(rv)
        rvAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                if (!rvDragHelper.isUserDraggingItem() || toPosition == 0) {
                    rv.postOnAnimation { rv.scrollToPosition(0) }
                }
            }
        })
    }

    private fun handleNewResults(response: Resource<List<HomeListItemModel>>) {
        when (response) {
            is Resource.Loading -> {
                // loading is not handled yet
            }
            is Resource.Success -> {
                updateListData(response)
            }
            is Resource.Error -> {
                Toast.makeText(context, R.string.refresh_ratios_error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateListData(response: Resource<List<HomeListItemModel>>) {
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
