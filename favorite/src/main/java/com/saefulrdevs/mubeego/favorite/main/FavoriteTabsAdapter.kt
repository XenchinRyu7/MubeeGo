package com.saefulrdevs.mubeego.favorite.main

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.saefulrdevs.mubeego.favorite.main.movies.FavoriteMoviesFragment
import com.saefulrdevs.mubeego.favorite.main.tvshows.FavoriteTvShowsFragment

class FavoriteTabsAdapter (activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when (position) {
            0 -> fragment = FavoriteMoviesFragment()
            1 -> fragment = FavoriteTvShowsFragment()
        }
        return fragment as Fragment
    }

    override fun getItemCount(): Int {
        return 2
    }
}