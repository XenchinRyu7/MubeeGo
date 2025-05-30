package com.saefulrdevs.mubeego.ui.main.detail.movie

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import com.saefulrdevs.mubeego.core.data.Resource
import com.saefulrdevs.mubeego.core.data.source.remote.response.CreditsResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.GenreResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.MovieDetailResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.WatchProvidersResponse
import com.saefulrdevs.mubeego.core.domain.model.Movie
import com.saefulrdevs.mubeego.core.domain.usecase.TmdbUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("BooleanMethodIsAlwaysInverted")
class MovieDetailViewModel(private val tmdbUseCase: TmdbUseCase) : ViewModel() {

    private val movie = MutableLiveData<Movie>()

    fun setMovie(movie: Movie) {
        this.movie.value = movie
    }

    fun getMovieDetail(movieId: Int): LiveData<Resource<Movie>> =
        tmdbUseCase.getMovieDetail(movieId.toString()).asLiveData()

    fun getMovieWatchProviders(movieId: Int) = tmdbUseCase.getMovieWatchProviders(movieId.toString())

    fun setFavorite(): Boolean {
        val movie = movie.value
        if (movie != null) {
            val newState = !movie.favorited
            tmdbUseCase.setFavoriteMovie(movie, newState)
            return newState
        }
        return false
    }

    fun isMovieFavorited(movieId: Int): LiveData<Boolean> =
        tmdbUseCase.getFavoriteMovie().asLiveData().map { list ->
            list.any { it.movieId == movieId }
        }

    private val _movieDetails = MutableLiveData<Map<Int, MovieDetailResponse>>()
    val movieDetails: LiveData<Map<Int, MovieDetailResponse>> = _movieDetails

    // Cache credits
    private val _movieCredits = MutableLiveData<Map<Int, CreditsResponse>>()
    val movieCredits: LiveData<Map<Int, CreditsResponse>> = _movieCredits

    // Cache watch providers
    private val _movieProviders = MutableLiveData<Map<Int, WatchProvidersResponse>>()
    val movieProviders: LiveData<Map<Int, WatchProvidersResponse>> = _movieProviders

    // Cache genres
    private val _genres = MutableLiveData<List<GenreResponse>>()
    val genres: LiveData<List<GenreResponse>> = _genres

    init {
        Log.d("MovieDetailViewModel", "ViewModel created: $this")
    }

    fun fetchMovieDetail(movieId: Int) {
        Log.d("MovieDetailViewModel", "fetchMovieDetail($movieId) called")
        CoroutineScope(Dispatchers.IO).launch {
            val result = tmdbUseCase.getMovieDetailRemote(movieId.toString())
            if (result != null) {
                val current = _movieDetails.value?.toMutableMap() ?: mutableMapOf()
                current[movieId] = result
                Log.d("MovieDetailViewModel", "MovieDetail cached for $movieId: $result")
                _movieDetails.postValue(current)
            }
        }
    }

    fun fetchMovieCredits(movieId: Int) {
        Log.d("MovieDetailViewModel", "fetchMovieCredits($movieId) called")
        CoroutineScope(Dispatchers.IO).launch {
            val result = tmdbUseCase.getMovieCreditsRemote(movieId.toString())
            if (result != null) {
                val current = _movieCredits.value?.toMutableMap() ?: mutableMapOf()
                current[movieId] = result
                Log.d("MovieDetailViewModel", "MovieCredits cached for $movieId: $result")
                _movieCredits.postValue(current)
            }
        }
    }

    fun fetchMovieProviders(movieId: Int) {
        Log.d("MovieDetailViewModel", "fetchMovieProviders($movieId) called")
        CoroutineScope(Dispatchers.IO).launch {
            val result = tmdbUseCase.getMovieWatchProvidersRemote(movieId.toString())
            if (result != null) {
                val current = _movieProviders.value?.toMutableMap() ?: mutableMapOf()
                current[movieId] = result
                Log.d("MovieDetailViewModel", "MovieProviders cached for $movieId: $result")
                _movieProviders.postValue(current)
            }
        }
    }

    fun fetchGenres() {
        Log.d("MovieDetailViewModel", "fetchGenres() called")
        CoroutineScope(Dispatchers.IO).launch {
            val result = tmdbUseCase.getGenresRemote()
            if (result != null) {
                Log.d("MovieDetailViewModel", "Genres cached: $result")
                _genres.postValue(result)
            }
        }
    }

    fun getCachedMovieDetail(movieId: Int): MovieDetailResponse? {
        return _movieDetails.value?.get(movieId)
    }
    fun getCachedMovieCredits(movieId: Int): CreditsResponse? {
        return _movieCredits.value?.get(movieId)
    }
    fun getCachedMovieProviders(movieId: Int): WatchProvidersResponse? {
        return _movieProviders.value?.get(movieId)
    }
    fun getCachedGenres(): List<GenreResponse>? {
        return _genres.value
    }

}