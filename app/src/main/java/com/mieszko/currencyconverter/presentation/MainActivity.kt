package com.mieszko.currencyconverter.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.presentation.home.HomeFragment
import com.mieszko.currencyconverter.presentation.tracking.TrackingFragment

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private companion object {
        const val TRACKING_FRAGMENT = "tracking_fragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.fragment_container_view, HomeFragment())
            }
        }
    }

    fun openSelectCurrenciesFragment() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            addToBackStack(TRACKING_FRAGMENT)
            replace(R.id.fragment_container_view, TrackingFragment())
        }
    }
}
