@file:Suppress("BooleanMethodIsAlwaysInverted")

package com.saefulrdevs.mubeego.ui.main.detail.tvseries

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.saefulrdevs.mubeego.core.domain.usecase.TmdbUseCase
import com.saefulrdevs.mubeego.core.data.source.remote.response.TvShowDetailResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.CreditsResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.WatchProvidersResponse
import com.saefulrdevs.mubeego.core.domain.usecase.FavoriteFirestoreUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class TvSeriesDetailViewModel(
    private val tmdbUseCase: TmdbUseCase,
    private val favoriteFirestoreUseCase: FavoriteFirestoreUseCase
) : ViewModel() {

//    private val tvShow = MutableLiveData<TvShow>()
//
//    fun setSelectedTvShow(tvShow: TvShow) {
//        this.tvShow.value = tvShow
//    }
//
//    fun getTvShowDetail(showId: Int): LiveData<Resource<TvShow>> =
//        tmdbUseCase.getTvShowDetail(showId.toString()).asLiveData()
//
//    fun getTvShowSeasons(showId: Int): LiveData<Resource<TvShowWithSeason>> =
//        tmdbUseCase.getTvShowWithSeason(showId.toString()).asLiveData()

    private val _tvShowDetails = MutableLiveData<Map<Int, TvShowDetailResponse>>()
    val tvShowDetails: LiveData<Map<Int, TvShowDetailResponse>> = _tvShowDetails

    private val _tvShowCredits = MutableLiveData<Map<Int, CreditsResponse>>()
    val tvShowCredits: LiveData<Map<Int, CreditsResponse>> = _tvShowCredits

    private val _tvShowProviders = MutableLiveData<Map<Int, WatchProvidersResponse>>()
    val tvShowProviders: LiveData<Map<Int, WatchProvidersResponse>> = _tvShowProviders

    private val _isFavorited = MutableLiveData<Boolean>()
    val isFavorited: LiveData<Boolean> get() = _isFavorited

    fun fetchTvShowDetail(showId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = tmdbUseCase.getTvShowDetailRemote(showId.toString())
            if (result != null) {
                val current = _tvShowDetails.value?.toMutableMap() ?: mutableMapOf()
                current[showId] = result
                _tvShowDetails.postValue(current)
            }
        }
    }

    fun fetchTvShowCredits(showId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = tmdbUseCase.getTvShowAggregateCreditsRemote(showId.toString())
            if (result != null) {
                val current = _tvShowCredits.value?.toMutableMap() ?: mutableMapOf()
                current[showId] = result
                _tvShowCredits.postValue(current)
            }
        }
    }

    fun fetchTvShowProviders(showId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = tmdbUseCase.getTvShowWatchProvidersRemote(showId.toString())
            if (result != null) {
                val current = _tvShowProviders.value?.toMutableMap() ?: mutableMapOf()
                current[showId] = result
                _tvShowProviders.postValue(current)
            }
        }
    }    fun fetchFavoriteStatus(tvShowId: Int) {
        viewModelScope.launch {
            try {
                val isFavorite = favoriteFirestoreUseCase.isTvShowFavorited(tvShowId)
                android.util.Log.d("TvSeriesDetailViewModel", "fetchFavoriteStatus for TV show $tvShowId: $isFavorite")
                _isFavorited.postValue(isFavorite)
            } catch (e: Exception) {
                android.util.Log.e("TvSeriesDetailViewModel", "Error fetching favorite status: ${e.message}", e)
            }
        }
    }fun toggleFavorite(tvShowId: Int) {
        viewModelScope.launch {
            try {
                val current = favoriteFirestoreUseCase.isTvShowFavorited(tvShowId)
                if (current) {
                    // Unfavorite
                    val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                    com.google.firebase.firestore.FirebaseFirestore.getInstance()
                        .collection("users").document(userId)
                        .collection("favorites_tv").document(tvShowId.toString())
                        .delete().await() // Ensure the operation completes before updating UI
                } else {
                    // Favorite
                    val userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                    com.google.firebase.firestore.FirebaseFirestore.getInstance()
                        .collection("users").document(userId)
                        .collection("favorites_tv").document(tvShowId.toString())
                        .set(mapOf("favorited" to true)).await() // Ensure the operation completes before updating UI
                }
                // Only update UI after Firestore operations complete
                fetchFavoriteStatus(tvShowId)
            } catch (e: Exception) {
                android.util.Log.e("TvSeriesDetailViewModel", "Error toggling favorite: ${e.message}", e)
            }
        }
    }

    fun getCachedTvShowDetail(showId: Int): TvShowDetailResponse? {
        return _tvShowDetails.value?.get(showId)
    }
    fun getCachedTvShowCredits(showId: Int): CreditsResponse? {
        return _tvShowCredits.value?.get(showId)
    }
    fun getCachedTvShowProviders(showId: Int): WatchProvidersResponse? {
        return _tvShowProviders.value?.get(showId)
    }

}