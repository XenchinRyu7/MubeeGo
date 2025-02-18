package com.saefulrdevs.mubeego.di

import com.saefulrdevs.mubeego.core.domain.usecase.TmdbInteractor
import com.saefulrdevs.mubeego.core.domain.usecase.TmdbUseCase
import com.saefulrdevs.mubeego.ui.movies.MoviesViewModel
import com.saefulrdevs.mubeego.ui.tvshows.TvShowsViewModel
import com.saefulrdevs.mubeego.ui.moviedetail.MovieDetailViewModel
import com.saefulrdevs.mubeego.ui.search.SearchViewModel
import com.saefulrdevs.mubeego.ui.tvshowdetail.TvShowDetailViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val useCaseModule = module {
    factory<TmdbUseCase> { TmdbInteractor(get()) }
}

val viewModelModule = module {
    viewModel { MoviesViewModel(get()) }
    viewModel { TvShowsViewModel(get()) }
    viewModel { MovieDetailViewModel(get()) }
    viewModel { TvShowDetailViewModel(get()) }
    viewModel { SearchViewModel(get()) }
}