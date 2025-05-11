package com.saefulrdevs.mubeego.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import android.os.Bundle
import android.os.Handler
import com.saefulrdevs.mubeego.core.domain.usecase.UserPreferencesUseCase
import com.saefulrdevs.mubeego.ui.authentication.AuthActivity
import com.saefulrdevs.mubeego.ui.main.MainNavigation
import org.koin.android.ext.android.inject

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val userPreferencesUseCase: UserPreferencesUseCase by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Terapkan theme mode dari preferences
        when (userPreferencesUseCase.getThemeMode()) {
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        val isUserLoggedIn = userPreferencesUseCase.getUser() != null

        Handler(mainLooper).postDelayed({
            val intent = if (isUserLoggedIn) {
                Intent(this@SplashActivity, MainNavigation::class.java)
            } else {
                Intent(this@SplashActivity, AuthActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, SPLASH_TIME)
    }

    companion object {
        const val SPLASH_TIME = 2000L
    }
}
