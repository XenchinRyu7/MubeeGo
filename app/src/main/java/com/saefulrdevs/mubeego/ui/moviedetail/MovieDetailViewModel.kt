@file:Suppress("BooleanMethodIsAlwaysInverted")

package com.saefulrdevs.mubeego.ui.moviedetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.saefulrdevs.mubeego.core.data.Resource
import com.saefulrdevs.mubeego.core.domain.model.Movie
import com.saefulrdevs.mubeego.core.domain.usecase.TmdbUseCase

//@HiltViewModel
@Suppress("BooleanMethodIsAlwaysInverted")
class MovieDetailViewModel //@Inject constructor
    (private val tmdbUseCase: TmdbUseCase) :
    ViewModel() {

    private val movie = MutableLiveData<Movie>()

    fun setMovie(movie: Movie) {
        this.movie.value = movie
    }

    fun getMovieDetail(movieId: Int): LiveData<Resource<Movie>> =
        tmdbUseCase.getMovieDetail(movieId.toString()).asLiveData()

    fun setFavorite(): Boolean {
        val movie = movie.value
        if (movie != null) {
            val newState = !movie.favorited
            tmdbUseCase.setFavoriteMovie(movie, newState)
            return newState
        }
        return false
    }
}