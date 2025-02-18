package com.saefulrdevs.mubeego.favorite.main.movies

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.saefulrdevs.mubeego.core.domain.model.Movie
import com.saefulrdevs.mubeego.core.domain.usecase.TmdbUseCase

//@HiltViewModel
class FavoriteMoviesViewModel //@Inject constructor
    (private val tmdbUseCase: TmdbUseCase): ViewModel(){
    fun getMovieFav(): LiveData<List<Movie>> =
        tmdbUseCase.getFavoriteMovie().asLiveData()
}