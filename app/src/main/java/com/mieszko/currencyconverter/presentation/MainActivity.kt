package com.mieszko.currencyconverter.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.presentation.home.HomeFragment
import com.mieszko.currencyconverter.presentation.tracking.TrackingFragment

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private companion object {
        const val TRACKING_FRAGMENT = "tracking_fragment"
    }

    private val bottomNavigation: BottomNavigationView by lazy { findViewById(R.id.bottom_navigation) }

    // TODO !!!
    // TODO INTRODUCE NAVIGATION COMPONENT, THAT WILL HELP ME MANAGING BOTTOM NAVIGATION WITH BACK PRESS!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        if (savedInstanceState == null) {
//            supportFragmentManager.commit {
//                setReorderingAllowed(true)
//                replace(R.id.fragment_container_view, HomeFragment())
//            }
//        }

        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.tab_calculator -> {
                    supportFragmentManager.commit {
//                        setReorderingAllowed(true)
                        replace(R.id.fragment_container_view, HomeFragment())
                    }
                    true
                }
                R.id.tab_currencies -> {
                    supportFragmentManager.commit {
//                        setReorderingAllowed(true)
                        addToBackStack(TRACKING_FRAGMENT)
                        replace(R.id.fragment_container_view, TrackingFragment())
                    }
                    true
                }
                R.id.tab_more -> {
                    // Respond to navigation item 2 click
                    true
                }
                else -> false
            }
        }


        bottomNavigation.setOnNavigationItemReselectedListener { item ->
            when (item.itemId) {
                R.id.tab_calculator -> {
                    // TODO ON RESELECTION CALL FRAGMENT METHOD TO CLICK REFRESH
                    Toast.makeText(this, "1", Toast.LENGTH_SHORT).show()
                }
                R.id.tab_currencies -> {
                    // TODO ON RESELECTION CLEAR SEARCH, HIDE KEYBOARD AND SCROLL TOP
                    Toast.makeText(this, "2", Toast.LENGTH_SHORT).show()
                }
                R.id.tab_more -> {
                    Toast.makeText(this, "3", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun openSelectCurrenciesFragment() {
//        supportFragmentManager.commit {
//            setReorderingAllowed(true)
//            addToBackStack(TRACKING_FRAGMENT)
//            replace(R.id.fragment_container_view, TrackingFragment())
//        }
    }
}
