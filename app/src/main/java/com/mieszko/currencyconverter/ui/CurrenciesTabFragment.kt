package com.mieszko.currencyconverter.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.mieszko.currencyconverter.R

class CurrenciesTabFragment : Fragment(R.layout.currencies_tab_fragment) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        childFragmentManager.commit {
            replace(R.id.currencies_header_container, CurrenciesHeaderFragment())
            replace(R.id.currencies_list_container, CurrenciesListFragment())
        }
    }
}