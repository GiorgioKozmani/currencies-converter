package com.mieszko.currencyconverter.presentation

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mieszko.currencyconverter.R
import com.mieszko.currencyconverter.data.persistance.ISharedPrefsManager
import org.koin.android.ext.android.inject
import java.util.Date

class MainActivity :
    AppCompatActivity(R.layout.activity_main),
    NavController.OnDestinationChangedListener {
    private val bottomNavigation: BottomNavigationView by lazy { findViewById(R.id.bottom_navigation) }
    private val sharedPrefsManager: ISharedPrefsManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupNavigation()

        markFirstAppOpening()
    }

    private fun setupNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val navController = navHostFragment.navController
        navController.addOnDestinationChangedListener(this)
        bottomNavigation.setupWithNavController(navController)

        // this is implemented so we're not recreating fragment if it's already selected
        bottomNavigation.setOnItemReselectedListener { item ->
            when (item.itemId) {
                R.id.homeFragment -> {
                    // TODO ON RESELECTION CALL FRAGMENT METHOD TO CLICK REFRESH / scroll to top?
                }
                R.id.selectionFragment -> {
                    // TODO ON RESELECTION CLEAR SEARCH, HIDE KEYBOARD AND SCROLL TOP
                }
            }
        }
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        // Managing the keyboard is never easy ;)
        if (destination.label != "HomeFragment") {
            currentFocus?.clearFocus()
            currentFocus?.hideKeyboard()
        }
    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun markFirstAppOpening() {
        val firstAppOpen = sharedPrefsManager.getDate(ISharedPrefsManager.Key.FirstAppOpenDate)
        if (firstAppOpen == null) {
            sharedPrefsManager.put(ISharedPrefsManager.Key.FirstAppOpenDate, Date())
        }
    }
}
