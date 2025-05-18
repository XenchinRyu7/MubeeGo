package com.saefulrdevs.mubeego.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.saefulrdevs.mubeego.core.data.Resource
import com.saefulrdevs.mubeego.core.domain.model.Movie
import com.saefulrdevs.mubeego.core.domain.model.SearchItem
import com.saefulrdevs.mubeego.core.domain.usecase.TmdbUseCase

class HomeViewModel
    (private val tmdbUseCase: TmdbUseCase) :
    ViewModel() {

    init {
        tmdbUseCase.observeFavoriteMoviesRealtime()
        tmdbUseCase.observeFavoriteTvShowsRealtime()
    }

    fun getNowPlaying(): LiveData<Resource<List<Movie>>> =
        tmdbUseCase.getNowPlayingMovies().asLiveData()

    fun getPopular(): LiveData<Resource<List<SearchItem>>> =
        tmdbUseCase.getPopular().asLiveData()

    fun getSearchResult(title: String): LiveData<Resource<List<SearchItem>>> =
        tmdbUseCase.getSearchResult(title).asLiveData()

    fun getUpcomingMoviesByDate(minDate: String, maxDate: String) =
        tmdbUseCase.getUpcomingMoviesByDate(minDate, maxDate).asLiveData()


}