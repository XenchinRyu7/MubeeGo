package com.saefulrdevs.mubeego.ui.main

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
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

            // Kalau cuma mau top padding (buat status bar), dan biarin bottom nempel:
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)

            insets
        }


        // Setup Toolbar
        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        // Setup Navigation
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val bottomNavigationView = binding.bottomNavigation
        bottomNavigationView.setupWithNavController(navController)

        // Fix: Use default navigation for all tabs, including Favorite
        bottomNavigationView.setOnItemSelectedListener { item ->
            navController.navigate(item.itemId)
            true
        }
    }
}