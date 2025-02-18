package com.saefulrdevs.mubeego.favorite.main.tvshows

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.saefulrdevs.mubeego.core.domain.model.TvShow
import com.saefulrdevs.mubeego.core.domain.usecase.TmdbUseCase

//@HiltViewModel
class FavoriteTvShowsViewModel //@Inject constructor
    (private val tmdbUseCase: TmdbUseCase): ViewModel() {
    fun getTvShowFav(): LiveData<List<TvShow>> =
        tmdbUseCase.getFavoriteTvShow().asLiveData()
}