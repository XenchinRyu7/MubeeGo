@file:Suppress("BooleanMethodIsAlwaysInverted")

package com.saefulrdevs.mubeego.ui.main.detail.tvseries

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.saefulrdevs.mubeego.core.data.Resource
import com.saefulrdevs.mubeego.core.domain.model.TvShow
import com.saefulrdevs.mubeego.core.domain.model.TvShowWithSeason
import com.saefulrdevs.mubeego.core.domain.usecase.TmdbUseCase
import com.saefulrdevs.mubeego.core.data.source.remote.response.TvShowDetailResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.CreditsResponse
import com.saefulrdevs.mubeego.core.data.source.remote.response.WatchProvidersResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TvSeriesDetailViewModel
    (private val tmdbUseCase: TmdbUseCase) :
    ViewModel() {

    private val tvShow = MutableLiveData<TvShow>()

    fun setSelectedTvShow(tvShow: TvShow) {
        this.tvShow.value = tvShow
    }

    fun getTvShowDetail(showId: Int): LiveData<Resource<TvShow>> =
        tmdbUseCase.getTvShowDetail(showId.toString()).asLiveData()

    fun getTvShowSeasons(showId: Int): LiveData<Resource<TvShowWithSeason>> =
        tmdbUseCase.getTvShowWithSeason(showId.toString()).asLiveData()

    fun setFavorite(): Boolean {
        val tvShow = tvShow.value
        if (tvShow != null) {
            val newState = !tvShow.favorited
            tmdbUseCase.setFavoriteTvShow(tvShow, newState)
            return newState
        }
        return false
    }

    private val _tvShowDetails = MutableLiveData<Map<Int, TvShowDetailResponse>>()
    val tvShowDetails: LiveData<Map<Int, TvShowDetailResponse>> = _tvShowDetails

    private val _tvShowCredits = MutableLiveData<Map<Int, CreditsResponse>>()
    val tvShowCredits: LiveData<Map<Int, CreditsResponse>> = _tvShowCredits

    private val _tvShowProviders = MutableLiveData<Map<Int, WatchProvidersResponse>>()
    val tvShowProviders: LiveData<Map<Int, WatchProvidersResponse>> = _tvShowProviders

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