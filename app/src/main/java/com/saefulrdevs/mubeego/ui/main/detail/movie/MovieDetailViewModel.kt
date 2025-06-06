package com.saefulrdevs.mubeego.ui.main.detail.movie

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.saefulrdevs.mubeego.core.data.Resource
import com.saefulrdevs.mubeego.core.data.source.remote.response.CreditsResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.GenreResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.MovieDetailResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.WatchProvidersResponse
import com.saefulrdevs.mubeego.core.domain.model.Movie
import com.saefulrdevs.mubeego.core.domain.usecase.FavoriteFirestoreUseCase
import com.saefulrdevs.mubeego.core.domain.usecase.TmdbUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MovieDetailViewModel(
    private val tmdbUseCase: TmdbUseCase,
    private val favoriteFirestoreUseCase: FavoriteFirestoreUseCase
) : ViewModel() {

    private val movie = MutableLiveData<Movie>()

    fun setMovie(movie: Movie) {
        Log.d("MovieDetailViewModel", "setMovie called: movieId=${movie.movieId}")
        this.movie.value = movie
    }

    fun getMovieDetail(movieId: Int): LiveData<Resource<Movie>> =
        tmdbUseCase.getMovieDetail(movieId.toString()).asLiveData()

    fun getMovieWatchProviders(movieId: Int) = tmdbUseCase.getMovieWatchProviders(movieId.toString())

    private val _movieDetails = MutableLiveData<Map<Int, MovieDetailResponse>>()
    val movieDetails: LiveData<Map<Int, MovieDetailResponse>> = _movieDetails

    private val _movieCredits = MutableLiveData<Map<Int, CreditsResponse>>()
    val movieCredits: LiveData<Map<Int, CreditsResponse>> = _movieCredits

    private val _movieProviders = MutableLiveData<Map<Int, WatchProvidersResponse>>()
    val movieProviders: LiveData<Map<Int, WatchProvidersResponse>> = _movieProviders

    private val _genres = MutableLiveData<List<GenreResponse>>()
    val genres: LiveData<List<GenreResponse>> = _genres

    private val _isFavorited = MutableLiveData<Boolean>()
    val isFavorited: LiveData<Boolean> get() = _isFavorited

    private val firestoreFavorite = MutableLiveData<Boolean>()

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

    fun observeFirestoreFavorite(movieId: Int) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .collection("favorites_movies")
            .document(movieId.toString())
            .addSnapshotListener { snapshot, _ ->
                firestoreFavorite.postValue(snapshot?.exists() == true)
            }
    }

    fun getFirestoreFavorite(): LiveData<Boolean> = firestoreFavorite

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
    }    fun fetchFavoriteStatus(movieId: Int) {
        viewModelScope.launch {
            try {
                val isFavorite = favoriteFirestoreUseCase.isMovieFavorited(movieId)
                Log.d("MovieDetailViewModel", "fetchFavoriteStatus for movie $movieId: $isFavorite")
                _isFavorited.postValue(isFavorite)
            } catch (e: Exception) {
                Log.e("MovieDetailViewModel", "Error fetching favorite status: ${e.message}", e)
            }
        }
    }fun toggleFavorite(movieId: Int) {
        viewModelScope.launch {
            try {
                val current = favoriteFirestoreUseCase.isMovieFavorited(movieId)
                if (current) {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                    Log.d("MovieDetailViewModel", "Removing movie $movieId from favorites")
                    FirebaseFirestore.getInstance()
                        .collection("users").document(userId)
                        .collection("favorites_movies").document(movieId.toString())
                        .delete().await()
                    Log.d("MovieDetailViewModel", "Movie $movieId removed from favorites")
                } else {
                    // Favorite
                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                    Log.d("MovieDetailViewModel", "Adding movie $movieId to favorites")
                    FirebaseFirestore.getInstance()
                        .collection("users").document(userId)
                        .collection("favorites_movies").document(movieId.toString())
                        .set(mapOf("favorited" to true)).await()
                    Log.d("MovieDetailViewModel", "Movie $movieId added to favorites")
                }
                // Force update UI
                _isFavorited.postValue(!current)
                fetchFavoriteStatus(movieId)
            } catch (e: Exception) {
                Log.e("MovieDetailViewModel", "Error toggling favorite: ${e.message}", e)
            }
        }
    }

}