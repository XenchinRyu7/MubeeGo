@file:Suppress("BooleanMethodIsAlwaysInverted")

package com.saefulrdevs.mubeego.ui.tvshowdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.saefulrdevs.mubeego.core.data.Resource
import com.saefulrdevs.mubeego.core.domain.model.TvShow
import com.saefulrdevs.mubeego.core.domain.model.TvShowWithSeason
import com.saefulrdevs.mubeego.core.domain.usecase.TmdbUseCase

class TvShowDetailViewModel
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
}