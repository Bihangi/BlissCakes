package com.blisscakes.app.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.blisscakes.app.R
import com.blisscakes.app.databinding.ActivityMainBinding
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private var cartBadge: BadgeDrawable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        setupNavigation()
        setupTheme()
        observeViewModel()
    }

    private fun setupNavigation() {
        val navController = findNavController(R.id.nav_host_fragment)

        // Setup bottom navigation
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_browse,
                R.id.navigation_cart,
                R.id.navigation_orders,
                R.id.navigation_profile
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNav.setupWithNavController(navController)

        // Setup cart badge
        cartBadge = binding.bottomNav.getOrCreateBadge(R.id.navigation_cart)
        cartBadge?.isVisible = false
    }

    private fun setupTheme() {
        // Auto theme based on system settings
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.cartItemCount.collect { count ->
                if (count > 0) {
                    cartBadge?.isVisible = true
                    cartBadge?.number = count
                } else {
                    cartBadge?.isVisible = false
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isConnected.collect { isConnected ->
                if (!isConnected) {
                    Snackbar.make(
                        binding.root,
                        "No internet connection. Some features may be limited.",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                // Navigate to search
                findNavController(R.id.nav_host_fragment).navigate(R.id.searchActivity)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}