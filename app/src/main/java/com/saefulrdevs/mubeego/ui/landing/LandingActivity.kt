package com.saefulrdevs.mubeego.ui.landing

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.core.domain.usecase.UserPreferencesUseCase
import com.saefulrdevs.mubeego.databinding.ActivityLandingBinding
import com.saefulrdevs.mubeego.ui.main.MainNavigation
import org.koin.android.ext.android.inject

class LandingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLandingBinding
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
            startActivity(Intent(this, com.saefulrdevs.mubeego.ui.authentication.AuthActivity::class.java))
            finish()
            return
        }

        val adapter = OnboardingPagerAdapter(this)
        binding.viewPagerOnboarding.adapter = adapter

        // Dot indicator setup
        val dots = arrayOfNulls<ImageView>(3)
        val layoutDots = findViewById<LinearLayout>(R.id.layoutDots)
        layoutDots.removeAllViews()
        for (i in dots.indices) {
            dots[i] = ImageView(this)
            dots[i]?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_inactive))
            val params = LinearLayout.LayoutParams(24, 24)
            params.setMargins(8, 0, 8, 0)
            layoutDots.addView(dots[i], params)
        }
        dots[0]?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_active))

        binding.viewPagerOnboarding.registerOnPageChangeCallback(object : androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                for (i in dots.indices) {
                    dots[i]?.setImageDrawable(ContextCompat.getDrawable(this@LandingActivity, if (i == position) R.drawable.dot_active else R.drawable.dot_inactive))
                }
                binding.btnNext.text = if (position == 2) "Masuk" else "Next"
            }
        })

        binding.btnNext.setOnClickListener {
            val pos = binding.viewPagerOnboarding.currentItem
            if (pos < 2) {
                binding.viewPagerOnboarding.currentItem = pos + 1
            } else {
                userPreferencesUseCase.setOnboardingShown(true)
                startActivity(Intent(this, com.saefulrdevs.mubeego.ui.authentication.AuthActivity::class.java))
                finish()
            }
        }
    }
}