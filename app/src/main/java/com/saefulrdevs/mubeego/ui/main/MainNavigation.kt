package com.saefulrdevs.mubeego.ui.main

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.databinding.ActivityMainNavigationBinding

class MainNavigation : AppCompatActivity() {

    private lateinit var binding: ActivityMainNavigationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)

            insets
        }

        // Setup Toolbar
        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        // Setup Navigation
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
        val bottomNavigationView = binding.bottomNavigation
        bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (
                destination.id == R.id.seeMoreFragment ||
                destination.id == R.id.navigation_detail_movie ||
                destination.id == R.id.navigation_detail_tv_series
            ) {
                bottomNavigationView.visibility = View.GONE
            } else {
                bottomNavigationView.visibility = View.VISIBLE
            }

            if (destination.id == R.id.navigation_search) {
                supportActionBar?.hide()
            } else {
                supportActionBar?.show()
            }

            if (destination.id == R.id.seeMoreFragment) {
                supportActionBar?.title = ""
            }
        }

        // Fix: Use default navigation for all tabs, including Favorite
        bottomNavigationView.setOnItemSelectedListener { item ->
            navController.navigate(item.itemId)
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_search -> {
                val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                val navController = navHostFragment.navController
                navController.navigate(R.id.navigation_search)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}