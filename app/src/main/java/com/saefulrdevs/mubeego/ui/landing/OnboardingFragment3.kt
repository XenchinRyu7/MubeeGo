package com.saefulrdevs.mubeego.ui.landing

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.saefulrdevs.mubeego.R
import com.saefulrdevs.mubeego.core.domain.usecase.UserPreferencesUseCase
import com.saefulrdevs.mubeego.ui.authentication.AuthActivity
import org.koin.android.ext.android.inject

class OnboardingFragment3 : Fragment() {
    private val userPreferencesUseCase: UserPreferencesUseCase by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding_3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnGetStarted = view.findViewById<Button>(R.id.btnGetStarted)
        val btnBack = view.findViewById<Button>(R.id.btnBack)
        val tvSkip = view.findViewById<TextView>(R.id.tvSkip)
        btnBack.setOnClickListener {
            (requireActivity() as? LandingActivity)?.binding?.viewPagerOnboarding?.setCurrentItem(1, true)
        }
        btnGetStarted.setOnClickListener {
            userPreferencesUseCase.setOnboardingShown(true)
            startActivity(Intent(requireContext(), AuthActivity::class.java))
            requireActivity().finish()
        }
    }
}
