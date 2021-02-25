package com.mieszko.currencyconverter.presentation.home.header

import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.common.Resource
import com.mieszko.currencyconverter.presentation.home.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class HomeHeaderFragment : Fragment(R.layout.currencies_header_fragment) {
    private val viewModel by sharedViewModel<HomeViewModel>()
    private lateinit var lastUpdated: TextView

    //todo think of the way to handle errors, maybe in parent fragment?
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TODO VIEWBINDING
        lastUpdated = view.findViewById(R.id.last_updated)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.getLastUpdatedLiveData()
            .observe(viewLifecycleOwner, { handleNewDate(it) })
    }

    private fun handleNewDate(updateDate: Resource<Date>) {
        when (updateDate) {
            is Resource.Loading -> {
                // loading not handled yet
            }
            is Resource.Success -> {
                //todo if today / yesterday then think of using these strings instead of full day
                lastUpdated.text = DateFormat.format("yyyy-MM-dd hh:mm:ss a", updateDate.data)
            }
            is Resource.Error -> {
                // error not handled yet
            }
        }
    }
}