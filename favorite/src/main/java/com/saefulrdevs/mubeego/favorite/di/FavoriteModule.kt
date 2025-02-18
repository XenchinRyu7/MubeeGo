package com.saefulrdevs.mubeego.favorite.di

import com.saefulrdevs.mubeego.favorite.main.movies.FavoriteMoviesViewModel
import com.saefulrdevs.mubeego.favorite.main.tvshows.FavoriteTvShowsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val favoriteModule = module {
    viewModel { FavoriteMoviesViewModel(get()) }
    viewModel { FavoriteTvShowsViewModel(get()) }
}