package com.saefulrdevs.mubeego.ui.movies

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.saefulrdevs.mubeego.core.data.Resource
import com.saefulrdevs.mubeego.core.domain.model.Movie
import com.saefulrdevs.mubeego.core.domain.usecase.TmdbUseCase

class MoviesViewModel(private val tmdbUseCase: TmdbUseCase) : ViewModel() {

    fun getDiscoverMovies(): LiveData<Resource<List<Movie>>> = liveData {
        emit(Resource.Loading())
        Log.d("MoviesViewModel", "Fetching movies...")

        try {
            tmdbUseCase.getDiscoverMovies().collect { resource ->
                when (resource) {
                    is Resource.Loading -> Log.d("MoviesViewModel", "Loading movies...")
                    is Resource.Success -> {
                        if (resource.data.isNullOrEmpty()) {
                            Log.w("MoviesViewModel", "Movies list is empty!")
                            emit(Resource.Error("No movies found"))
                        } else {
                            Log.d("MoviesViewModel", "Movies fetched successfully: ${resource.data!!.size} movies")
                            emit(resource)
                        }
                    }
                    is Resource.Error -> {
                        Log.e("MoviesViewModel", "Error fetching movies: ${resource.message}")
                        emit(resource)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("MoviesViewModel", "Exception occurred: ${e.message}")
            emit(Resource.Error(e.message ?: "Unknown Error"))
        }
    }
}

