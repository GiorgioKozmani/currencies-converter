package com.mieszko.currencyconverter.presentation.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.presentation.MainActivity
import com.mieszko.currencyconverter.presentation.home.header.HomeHeaderFragment
import com.mieszko.currencyconverter.presentation.home.list.HomeListFragment

class HomeFragment : Fragment(R.layout.home_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        childFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.currencies_header_container, HomeHeaderFragment())
            replace(R.id.currencies_list_container, HomeListFragment())
        }

        //todo remake
        view.findViewById<View>(R.id.select_currencies_fab).setOnClickListener {
            (activity as MainActivity).openSelectCurrenciesFragment()
        }
    }
}