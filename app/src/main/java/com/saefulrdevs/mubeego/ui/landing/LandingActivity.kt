package com.saefulrdevs.mubeego.ui.landing

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.saefulrdevs.mubeego.core.domain.usecase.UserPreferencesUseCase
import com.saefulrdevs.mubeego.databinding.ActivityLandingBinding
import com.saefulrdevs.mubeego.ui.authentication.AuthActivity
import org.koin.android.ext.android.inject

class LandingActivity : AppCompatActivity() {

    internal lateinit var binding: ActivityLandingBinding
    private val userPreferencesUseCase: UserPreferencesUseCase by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (userPreferencesUseCase.isOnboardingShown()) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
            return
        }

        val adapter = OnboardingPagerAdapter(this)
        binding.viewPagerOnboarding.adapter = adapter

        binding.viewPagerOnboarding.isUserInputEnabled = false
    }
}