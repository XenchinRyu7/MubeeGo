package com.saefulrdevs.mubeego.ui.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.saefulrdevs.mubeego.core.data.Resource
import com.saefulrdevs.mubeego.core.domain.model.Movie
import com.saefulrdevs.mubeego.core.domain.usecase.TmdbUseCase

class MoviesViewModel
    (private val tmdbUseCase: TmdbUseCase) :
    ViewModel() {

    fun getDiscoverMovies(): LiveData<Resource<List<Movie>>> =
        tmdbUseCase.getDiscoverMovies().asLiveData()

    fun clearMovies() = tmdbUseCase.clearMovies()
}