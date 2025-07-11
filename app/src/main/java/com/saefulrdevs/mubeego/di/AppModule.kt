package com.saefulrdevs.mubeego.di

import com.saefulrdevs.mubeego.core.domain.usecase.AuthInteractor
import com.saefulrdevs.mubeego.core.domain.usecase.AuthUseCase
import com.saefulrdevs.mubeego.core.domain.usecase.FavoriteFirestoreInteractor
import com.saefulrdevs.mubeego.core.domain.usecase.FavoriteFirestoreUseCase
import com.saefulrdevs.mubeego.core.domain.usecase.HonorableInteractor
import com.saefulrdevs.mubeego.core.domain.usecase.HonorableUseCase
import com.saefulrdevs.mubeego.core.domain.usecase.PlaylistInteractor
import com.saefulrdevs.mubeego.core.domain.usecase.PlaylistUseCase
import com.saefulrdevs.mubeego.core.domain.usecase.TmdbInteractor
import com.saefulrdevs.mubeego.core.domain.usecase.TmdbUseCase
import com.saefulrdevs.mubeego.core.domain.usecase.UserPreferencesInteractor
import com.saefulrdevs.mubeego.core.domain.usecase.UserPreferencesUseCase
import com.saefulrdevs.mubeego.ui.authentication.AuthViewModel
import com.saefulrdevs.mubeego.ui.main.movies.MoviesViewModel
import com.saefulrdevs.mubeego.ui.main.tvshows.TvSeriesViewModel
import com.saefulrdevs.mubeego.ui.main.detail.movie.MovieDetailViewModel
import com.saefulrdevs.mubeego.ui.main.home.HomeViewModel
import com.saefulrdevs.mubeego.ui.main.detail.tvseries.TvSeriesDetailViewModel
import com.saefulrdevs.mubeego.ui.main.favorite.FavoriteViewModel
import com.saefulrdevs.mubeego.ui.main.home.HonorableViewModel
import com.saefulrdevs.mubeego.ui.main.playlist.PlaylistViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val useCaseModule = module {
    factory<TmdbUseCase> { TmdbInteractor(get()) }
    factory<AuthUseCase> { AuthInteractor(get()) }
    factory<UserPreferencesUseCase> { UserPreferencesInteractor(get()) }
    factory<PlaylistUseCase> { PlaylistInteractor(get()) }
    factory<FavoriteFirestoreUseCase> { FavoriteFirestoreInteractor(get(), get()) }
    factory<HonorableUseCase> { HonorableInteractor(get()) }
}

val viewModelModule = module {
    viewModel { MoviesViewModel(get()) }
    viewModel { TvSeriesViewModel(get()) }
    viewModel { MovieDetailViewModel(get(), get()) }
    viewModel { TvSeriesDetailViewModel(get(), get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { AuthViewModel(get(), get()) }
    viewModel { FavoriteViewModel(get()) }
    viewModel { PlaylistViewModel(get(), get()) }
    viewModel { HonorableViewModel(get()) }
}
