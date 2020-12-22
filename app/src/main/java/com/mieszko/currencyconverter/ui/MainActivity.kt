package com.mieszko.currencyconverter.ui

import android.app.Activity
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.data.model.CurrencyListItemModel
import com.mieszko.currencyconverter.data.model.Resource
import com.mieszko.currencyconverter.viewmodel.CurrenciesViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class MainActivity : AppCompatActivity() {

    //todo interface
    private var rvAdapter: CurrenciesAdapter? = null
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
                .observe(this@MainActivity, Observer() {
                    handleNewResults(it)
                })

            getLastUpdatedLiveData()
                .observe(this@MainActivity, Observer() {
                    handleNewDate(it)
                })
        }
    }

    private fun setupRecyclerView() {
        rvAdapter = CurrenciesAdapter(viewModel)
            .apply { setHasStableIds(true) }

        currencies_list?.run {
            layoutManager = LinearLayoutManager(context)
            adapter = rvAdapter
            setHasFixedSize(true)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    (getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
                        .hideSoftInputFromWindow(currentFocus?.windowToken, 0)
                }
            })
        }
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

    private fun updateListData(response: Resource<List<CurrencyListItemModel>>) {
        response.data?.let { newItems ->
            rvAdapter?.refreshData(newItems)
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
                last_updated.text = DateFormat.format("yyyy-MM-dd hh:mm:ss a", updateDate.data)
            }
            is Resource.Error -> {
            }
        }
    }

}
