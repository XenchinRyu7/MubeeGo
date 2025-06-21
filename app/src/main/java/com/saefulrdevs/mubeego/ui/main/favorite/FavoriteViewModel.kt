package com.saefulrdevs.mubeego.ui.main.favorite

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import com.saefulrdevs.mubeego.core.domain.model.SearchItem
import com.saefulrdevs.mubeego.core.domain.usecase.FavoriteFirestoreUseCase
import com.saefulrdevs.mubeego.core.util.DataMapper

class FavoriteViewModel(
    private val favoriteFirestoreUseCase: FavoriteFirestoreUseCase
) : ViewModel() {
    private val _favoriteList = MediatorLiveData<List<SearchItem>>()
    val favoriteList: LiveData<List<SearchItem>> get() = _favoriteList
    private var favoriteRealtimeJob: kotlinx.coroutines.Job? = null

    init {
        observeFavoritesRealtime()
    }

    fun refreshFavoriteList() {
        observeFavoritesRealtime()
    }

    private fun observeFavoritesRealtime() {
        favoriteRealtimeJob?.cancel()

        // Untuk memastikan kita mendapatkan updates yang konsisten, kita akan mengumpulkan
        // data dari kedua Flow (Movies dan TV Shows) secara terpisah dan menggabungkannya

        // Pertama, kita observasi movie IDs
        favoriteRealtimeJob = viewModelScope.launch {
            // Melakukan collect untuk kedua Flow (Movies dan TV Shows)
            launch {
                favoriteFirestoreUseCase.observeFavoriteMovieIds().collect { movieIds ->
                    Log.d("FavoriteViewModel", "Movie IDs updated: $movieIds")
                    updateFavoriteList()
                }
            }

            launch {
                favoriteFirestoreUseCase.observeFavoriteTvShowIds().collect { tvIds ->
                    Log.d("FavoriteViewModel", "TV IDs updated: $tvIds")
                    updateFavoriteList()
                }
            }
        }
    }

    private suspend fun updateFavoriteList() {
        try {
            // Dapatkan daftar ID terlebih dahulu
            val movieIds =
                favoriteFirestoreUseCase.observeFavoriteMovieIds().firstOrNull() ?: emptyList()
            val tvIds =
                favoriteFirestoreUseCase.observeFavoriteTvShowIds().firstOrNull() ?: emptyList()

            Log.d(
                "FavoriteViewModel",
                "Updating list with ${movieIds.size} movies and ${tvIds.size} TV shows"
            )

            // Get movie details
            val movieItems = movieIds.mapNotNull { id ->
                try {
                    val detail = favoriteFirestoreUseCase.getMovieDetailRemote(id.toString())
                    detail?.let { DataMapper.movieDetailResponseToSearchItem(it) }
                } catch (e: Exception) {
                    Log.e("FavoriteViewModel", "Error getting movie details for ID: $id", e)
                    null
                }
            }

            // Get TV details
            val tvItems = tvIds.mapNotNull { id ->
                try {
                    val detail = favoriteFirestoreUseCase.getTvShowDetailRemote(id.toString())
                    detail?.let { DataMapper.tvShowDetailResponseToSearchItem(it) }
                } catch (e: Exception) {
                    Log.e("FavoriteViewModel", "Error getting TV details for ID: $id", e)
                    null
                }
            }

            // Combine and sort the results
            val combined = (movieItems + tvItems).sortedByDescending { it.voteAverage }
            _favoriteList.postValue(combined)
        } catch (e: Exception) {
            Log.e("FavoriteViewModel", "Error updating favorite list", e)
        }
    }
}
