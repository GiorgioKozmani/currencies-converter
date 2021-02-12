package com.mieszko.currencyconverter.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.ui.MainActivity
import com.mieszko.currencyconverter.ui.main.list.HomeListFragment

class HomeFragment : Fragment(R.layout.currencies_tab_fragment) {

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