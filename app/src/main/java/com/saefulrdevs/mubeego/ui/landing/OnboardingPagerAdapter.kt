package com.saefulrdevs.mubeego.ui.landing

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2

class OnboardingPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int = 3
    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> OnboardingFragment1()
        1 -> OnboardingFragment2()
        2 -> OnboardingFragment3()
        else -> throw IllegalArgumentException("Invalid position")
    }

    fun goToPage(activity: FragmentActivity, page: Int) {
        val landingActivity = activity as? LandingActivity
        landingActivity?.binding?.viewPagerOnboarding?.setCurrentItem(page, true)
    }
}
