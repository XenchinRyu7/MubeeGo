package com.saefulrdevs.mubeego.ui.main.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.saefulrdevs.mubeego.core.domain.model.Movie
import com.saefulrdevs.mubeego.core.domain.usecase.TmdbUseCase

class FavoriteMoviesViewModel(
    private val tmdbUseCase: TmdbUseCase
) : ViewModel() {
    fun getMovieFav(): LiveData<List<Movie>> =
        tmdbUseCase.getFavoriteMovie().asLiveData()
}
