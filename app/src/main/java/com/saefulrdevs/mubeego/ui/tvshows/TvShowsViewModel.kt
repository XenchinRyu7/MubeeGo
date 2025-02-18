package com.saefulrdevs.mubeego.ui.tvshows

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.saefulrdevs.mubeego.core.data.Resource
import com.saefulrdevs.mubeego.core.domain.model.TvShow
import com.saefulrdevs.mubeego.core.domain.usecase.TmdbUseCase

//@HiltViewModel
class TvShowsViewModel //@Inject constructor
    (private val tmdbUseCase: TmdbUseCase) :
    ViewModel() {

    fun getDiscoverTvShow(): LiveData<Resource<List<TvShow>>> =
        tmdbUseCase.getDiscoverTvShow().asLiveData()
}