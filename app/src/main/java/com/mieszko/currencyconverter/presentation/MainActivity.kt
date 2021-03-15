package com.mieszko.currencyconverter.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mieszko.currencyconverter.R


class MainActivity : AppCompatActivity(R.layout.activity_main) {
    private val bottomNavigation: BottomNavigationView by lazy { findViewById(R.id.bottom_navigation) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        bottomNavigation.setupWithNavController(navHostFragment.navController)

        // this is implemented so we're not recreating fragment if it's already selected
        bottomNavigation.setOnNavigationItemReselectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    // TODO ON RESELECTION CALL FRAGMENT METHOD TO CLICK REFRESH
//                    Toast.makeText(this, "1", Toast.LENGTH_SHORT).show()
                }
                R.id.trackingFragment -> {
                    // TODO ON RESELECTION CLEAR SEARCH, HIDE KEYBOARD AND SCROLL TOP
//                    Toast.makeText(this, "2", Toast.LENGTH_SHORT).show()
                }
//                R.id.tab_more -> {
//                    Toast.makeText(this, "3", Toast.LENGTH_SHORT).show()
//                }
            }
        }
    }

}
