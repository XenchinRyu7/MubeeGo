package com.saefulrdevs.mubeego.ui.main

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.databinding.ActivityMainNavigationBinding
import androidx.core.content.edit
import com.saefulrdevs.mubeego.core.domain.usecase.UserPreferencesUseCase
import com.saefulrdevs.mubeego.core.util.fetchUserDataFromFirestore
import com.saefulrdevs.mubeego.ui.common.AdDialogFragment
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainNavigation : AppCompatActivity() {

    private lateinit var binding: ActivityMainNavigationBinding
    private val userPreferencesUseCase: UserPreferencesUseCase by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
            val currentDest = navHostFragment.navController.currentDestination
            val isFullScreen = currentDest?.id == R.id.seeMoreFragment ||
                    currentDest?.id == R.id.navigation_detail_movie ||
                    currentDest?.id == R.id.navigation_detail_tv_series ||
                    currentDest?.id == R.id.navigation_search ||
                    currentDest?.id == R.id.navigation_settings ||
                    currentDest?.id == R.id.profileUpdateFragment ||
                    currentDest?.id == R.id.navigation_playlist_detail ||
                    currentDest?.id == R.id.navigation_forgot_password ||
                    currentDest?.id == R.id.navigation_forgot_password_confirmation
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                if (isFullScreen) systemBars.bottom else 0
            )

            insets
        }

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_favorite,
                R.id.navigation_playlist,
                R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        val bottomNavigationView = binding.bottomNavigation
        bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (
                destination.id == R.id.seeMoreFragment ||
                destination.id == R.id.navigation_detail_movie ||
                destination.id == R.id.navigation_detail_tv_series ||
                destination.id == R.id.navigation_search ||
                destination.id == R.id.navigation_settings ||
                destination.id == R.id.profileUpdateFragment ||
                destination.id == R.id.navigation_playlist_detail ||
                destination.id == R.id.navigation_forgot_password ||
                destination.id == R.id.navigation_forgot_password_confirmation
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

            invalidateOptionsMenu()
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            navController.navigate(item.itemId)
            true
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val prefs = getSharedPreferences("mubeego_prefs", MODE_PRIVATE)
            val alreadyAsked = prefs.getBoolean("notification_permission_requested", false)
            if (!alreadyAsked) {
                if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
                }
                prefs.edit { putBoolean("notification_permission_requested", true) }
            }
        }

        intent?.data?.let { data ->
            if (data.scheme == "mubeego" && data.host == "playlist") {
                val segments = data.pathSegments
                if (segments.size >= 2) {
                    val userId = segments[0]
                    val playlistId = segments[1]
                    val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
                    val navController = navHostFragment.navController
                    val bundle = Bundle().apply {
                        putString("userId", userId)
                        putString("playlistId", playlistId)
                    }
                    navController.navigate(R.id.navigation_playlist_detail, bundle)
                }
            }
        }

        val user = userPreferencesUseCase.getUser()
        val uid = user?.uid
        if (uid != null) {
            lifecycleScope.launch {
                val userData = fetchUserDataFromFirestore(uid)
                if (userData != null && !userData.isPremium) {
                    // Prevent multiple AdDialogFragment instances
                    val existing = supportFragmentManager.findFragmentByTag("ad_dialog")
                    if (existing == null) {
                        AdDialogFragment().show(supportFragmentManager, "ad_dialog")
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        val currentDest = navController.currentDestination
        val isHome = currentDest?.id == R.id.navigation_home
        menu?.findItem(R.id.menu_search)?.isVisible = isHome
        return super.onPrepareOptionsMenu(menu)
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

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}

