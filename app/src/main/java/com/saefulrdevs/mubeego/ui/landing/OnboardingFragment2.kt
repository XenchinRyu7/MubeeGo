package com.saefulrdevs.mubeego.ui.landing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.saefulrdevs.mubeego.R

class OnboardingFragment2 : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding_2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnNext = view.findViewById<Button>(R.id.btnNext)
        val btnBack = view.findViewById<Button>(R.id.btnBack)
        val tvSkip = view.findViewById<TextView>(R.id.tvSkip)
        btnBack.setOnClickListener {
            (requireActivity() as? LandingActivity)?.binding?.viewPagerOnboarding?.setCurrentItem(0, true)
        }
        btnNext.setOnClickListener {
            (requireActivity() as? LandingActivity)?.binding?.viewPagerOnboarding?.setCurrentItem(2, true)
        }
        tvSkip.setOnClickListener {
            requireActivity().finish()
        }
    }
}
