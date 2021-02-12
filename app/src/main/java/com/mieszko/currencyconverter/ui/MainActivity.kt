package com.mieszko.currencyconverter.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.ui.main.HomeFragment
import com.mieszko.currencyconverter.ui.selection.SelectionFragmentEpoxy

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private companion object{
        const val MAIN_FRAGMENT = "main_fragment"
        const val SELECTION_FRAGMENT = "selection_fragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
//                addToBackStack(MAIN_FRAGMENT)
                add(R.id.fragment_container_view, HomeFragment())
            }
        }
    }

    //rename to manage?
    fun openSelectCurrenciesFragment(){
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            addToBackStack(SELECTION_FRAGMENT)
//            add(R.id.fragment_container_view, SelectionFragment())
            add(R.id.fragment_container_view, SelectionFragmentEpoxy())
        }
    }
}
