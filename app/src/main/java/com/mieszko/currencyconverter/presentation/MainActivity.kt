package com.mieszko.currencyconverter.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.presentation.home.HomeFragment
import com.mieszko.currencyconverter.presentation.tracking.TrackingFragment

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private companion object {
        const val SELECTION_FRAGMENT = "selection_fragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(R.id.fragment_container_view, HomeFragment())
            }
        }
    }

    fun openSelectCurrenciesFragment() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            addToBackStack(SELECTION_FRAGMENT)
            add(R.id.fragment_container_view, TrackingFragment())
        }
    }
}
